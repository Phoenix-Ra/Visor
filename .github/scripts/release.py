#!/usr/bin/env python3
"""Helper of .github/workflows/release.yml and publish.yml, settings in .github/release.toml.

    release.py config <key>                      one setting, e.g. build.java
    release.py collect                           copy the release jars of [build] jars into build/release-assets/
    release.py matrix <assets.json> --tag T      release assets -> publish matrix (outputs: matrix, version_type)
               [--versions FILE] [--only GLOB]
    release.py meta <jar> [<classifier jar>...]  mc-publish inputs of one release jar (outputs: name, version,
               --versions FILE [--version-type T] [--dir D]   loaders, game_versions, java, dependencies, filter,
                                                              files, downloads = "<asset> <path>" lines)
"""
import argparse
import fnmatch
import json
import os
import re
import shutil
import sys
import tomllib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SETTINGS = ROOT / ".github" / "release.toml"
NAME_SHAPE = "<base>-<mod version>+mc<minecraft version>-<loader>[-<classifier>].jar"


# ---- GitHub Actions plumbing -------------------------------------------------------------------
def warn(message):
    print(f"::warning::{message}")


def fail(message):
    print(f"::error::{message}")
    sys.exit(1)


def append(env_file, text):
    path = os.environ.get(env_file)
    if path:
        with open(path, "a", encoding="utf-8") as f:
            f.write(text)
    else:
        sys.stdout.write(text)


def output(**values):
    """Step outputs, lists become one entry per line (the mc-publish list syntax)."""
    lines = []
    for key, value in values.items():
        if isinstance(value, (list, tuple)):
            value = "\n".join(str(v) for v in value)
        elif not isinstance(value, str):
            value = json.dumps(value)
        if "\n" in value:
            lines.append(f"{key}<<EOF_{key}\n{value}\nEOF_{key}")
        else:
            lines.append(f"{key}={value}")
    append("GITHUB_OUTPUT", "\n".join(lines) + "\n")


def summary(markdown):
    append("GITHUB_STEP_SUMMARY", markdown + "\n")


def settings(key=None, default=None):
    with open(SETTINGS, "rb") as f:
        node = tomllib.load(f)
    if key is None:
        return node
    for part in key.split("."):
        if not isinstance(node, dict) or part not in node:
            return default
        node = node[part]
    return node


# ---- Release assets ----------------------------------------------------------------------------
def asset_name():
    """Regex of a release asset name, the configured classifiers form an optional suffix."""
    classifiers = [re.escape(c) for c in settings("publish.modrinth_classifiers", [])]
    suffix = f"(?:-(?P<classifier>{'|'.join(classifiers)}))?" if classifiers else "(?P<classifier>)"
    return re.compile(r"^(?P<base>.+?)-(?P<modver>\d[^+]*)\+mc(?P<mc>.+?)-(?P<loader>[^-]+?)" + suffix + r"\.jar$")


def group_assets(names, pattern):
    """Release jars by version: the main jar with its classifier jars, warns about incomplete sets."""
    entries, extras = {}, {}
    for name in sorted(names):
        match = pattern.match(name)
        if not match:
            continue
        key = (match["base"], match["modver"], match["mc"], match["loader"])
        if match["classifier"]:
            extras.setdefault(key, []).append(name)
        else:
            fields = {k: v for k, v in match.groupdict().items() if k != "classifier"}
            entries[key] = {"file": name, "extra": [], **fields}
    for key, names in extras.items():
        if key in entries:
            entries[key]["extra"] = names
        else:
            warn(f"{', '.join(names)}: no main jar to publish with")
    classifiers = settings("publish.modrinth_classifiers", [])
    for entry in entries.values():
        have = {pattern.match(name)["classifier"] for name in entry["extra"]}
        for classifier in classifiers:
            if classifier not in have:
                warn(f"No {classifier} jar for {entry['file']}")
    return list(entries.values())


def version_key(version):
    return tuple(int(part) if part.isdigit() else 0 for part in re.split(r"[.\-]", version))


def entry_key(entry):
    return version_key(entry["mc"]), entry["loader"]


# ---- Version table -----------------------------------------------------------------------------
def load_versions(path):
    """The version table of the project, {} when the file is not there."""
    path = Path(path) if path else None
    if not path or not path.is_file():
        return {}
    with open(path, "rb") as f:
        return tomllib.load(f)


def split_top_level(spec):
    """'[1.20,1.20.1],[1.21,)' -> ['[1.20,1.20.1]', '[1.21,)']"""
    parts, depth, current = [], 0, ""
    for ch in spec:
        if ch in "[(":
            depth += 1
        elif ch in "])":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append(current)
            current = ""
        else:
            current += ch
    parts.append(current)
    return [p.strip() for p in parts if p.strip()]


def maven_range(spec):
    """Maven version range -> mc-publish ranges (one per alternative), same syntax as Fabric's."""
    ranges = []
    for part in split_top_level(spec):
        if part[0] not in "[(":
            ranges.append(part)  # bare version: exactly that one
            continue
        body = part[1:-1]
        if "," not in body:
            ranges.append(body.strip())  # [1.20.2]
            continue
        low, high = (s.strip() for s in body.split(",", 1))
        terms = []
        if low:
            terms.append((">=" if part[0] == "[" else ">") + low)
        if high:
            terms.append(("<=" if part[-1] == "]" else "<") + high)
        ranges.append(" ".join(terms) if terms else "*")
    return ranges


def version_ranges(value):
    """A range of the version table -> mc-publish ranges, Maven intervals become comparator ranges."""
    ranges = []
    for spec in value if isinstance(value, list) else [value]:
        spec = str(spec).strip()
        if spec:
            ranges += maven_range(spec) if spec[0] in "[(" else [spec]
    return ranges


def check_targets(entries, versions):
    """Warn when [targets] expects a jar that is not there, or a jar is not in [targets]."""
    targets = versions.get("targets")
    if not targets:
        return
    found = {(entry["mc"], entry["loader"]) for entry in entries}
    expected = {(mc, loader) for mc, loaders in targets.items() for loader in loaders}
    for mc, loader in sorted(expected - found, key=lambda pair: (version_key(pair[0]), pair[1])):
        warn(f"[targets] expects a jar for Minecraft {mc} {loader}, there is none")
    for mc, loader in sorted(found - expected, key=lambda pair: (version_key(pair[0]), pair[1])):
        warn(f"The jar for Minecraft {mc} {loader} is not in [targets]")


# ---- Commands ----------------------------------------------------------------------------------
def cmd_config(args):
    value = settings(args.key)
    if value is None:
        fail(f"{args.key} is not set in {SETTINGS.relative_to(ROOT)}")
    print(value if isinstance(value, str) else json.dumps(value))


def cmd_collect(args):
    jars_dir = ROOT / settings("build.jars", "build/libs")
    exclude = settings("build.exclude", [])
    pattern = asset_name()
    target = ROOT / "build" / "release-assets"
    target.mkdir(parents=True, exist_ok=True)
    collected = []
    for jar in sorted(jars_dir.glob("*.jar")) if jars_dir.is_dir() else []:
        if any(fnmatch.fnmatchcase(jar.name, p) for p in exclude):
            print(f"  skip {jar.name}")
            continue
        if not pattern.match(jar.name):
            warn(f"{jar.name} is not named {NAME_SHAPE}, publish.yml will skip it")
        # gh release upload reads "file#label", keep asset names to safe characters
        name = re.sub(r"[^A-Za-z0-9._+-]", ".", jar.name)
        shutil.copy2(jar, target / name)
        collected.append(name)
        print(f"  {jar.name} -> build/release-assets/{name}")
    if collected:
        summary("### Release jars\n" + "".join(f"- `{name}`\n" for name in collected))
    else:
        warn(f"No release jars in {jars_dir}")
    entries = group_assets(collected, pattern)
    check_targets(entries, load_versions(ROOT / settings("publish.versions.file", "")))
    output(found=str(bool(collected)).lower(), count=len(collected))


def version_type(tag):
    """Release channel from the tag name."""
    tag = tag.lower()
    if any(word in tag for word in ("snapshot", "alpha", "pre")):
        return "alpha"
    if any(word in tag for word in ("beta", "rc")):
        return "beta"
    return "release"


def cmd_matrix(args):
    with open(args.assets, encoding="utf-8") as f:
        data = json.load(f)
    assets = data.get("assets", []) if isinstance(data, dict) else data
    names = [asset["name"] if isinstance(asset, dict) else str(asset) for asset in assets]
    exclude = settings("build.exclude", [])
    loaders = settings("publish.loaders", {})
    pattern = asset_name()
    accepted = []
    for name in sorted(names):
        match = pattern.match(name)
        if not match:
            print(f"  skip {name}: not named {NAME_SHAPE}")
        elif any(fnmatch.fnmatchcase(name, p) for p in exclude):
            print(f"  skip {name}: excluded by build.exclude")
        elif loaders and match["loader"] not in loaders:
            print(f"  skip {name}: no publish.loaders entry for '{match['loader']}'")
        else:
            accepted.append(name)
    entries = group_assets(accepted, pattern)
    check_targets(entries, load_versions(args.versions))
    if args.only:
        for entry in entries:
            if not fnmatch.fnmatchcase(entry["file"], args.only):
                print(f"  skip {entry['file']}: does not match '{args.only}'")
        entries = [entry for entry in entries if fnmatch.fnmatchcase(entry["file"], args.only)]
    if not entries:
        fail(f"No publishable jars on release {args.tag}, run the Release build workflow first")
    entries.sort(key=entry_key)
    for entry in entries:
        print(f"  {entry['file']}" + "".join(f" + {extra}" for extra in entry["extra"]))
    output(matrix=json.dumps(entries), version_type=version_type(args.tag), count=len(entries))


def cmd_meta(args):
    pattern = asset_name()
    assets = [Path(f).name for f in args.files]
    primary = assets[0]
    match = pattern.match(primary)
    if not match or match["classifier"]:
        fail(f"{primary} is not a main jar named {NAME_SHAPE}")
    mc, loader = match["mc"], match["loader"]
    versions = load_versions(args.versions)
    if not versions:
        fail(f"{args.versions} is missing or empty, it should be the version table the release was built from")
    table = versions.get(mc, {})

    range_key = settings(f"publish.versions.range.{loader}")
    if range_key:
        if range_key not in table:
            fail(f'["{mc}"] of {args.versions} has no {range_key}, needed for {primary}')
        game_versions = version_ranges(table[range_key])
    else:
        game_versions = [mc]  # no range configured for this loader: the build version only
    java_key = settings("publish.versions.java")
    java = str(table[java_key]) if java_key and java_key in table else ""
    loaders = settings(f"publish.loaders.{loader}", [loader])
    dependencies = settings(f"publish.dependencies.{loader}", [])
    game_filter = settings("publish.game_version_filter", "releases")

    fields = {
        "mod_name": versions.get("mod_name") or match["base"],
        "mod_version": match["modver"],
        "mc": mc,
        "loader": loader,
        "loaders": ", ".join(loaders),
    }
    try:
        name = settings("publish.name", "{mod_name} {mod_version} - MC {mc} ({loader})").format(**fields)
        version = settings("publish.version", "{mod_version}+mc{mc}-{loader}").format(**fields)
    except (KeyError, IndexError, ValueError) as e:
        placeholders = " ".join("{" + key + "}" for key in fields)
        fail(f"publish.name / publish.version: bad placeholder {e}, available: {placeholders}")

    # The Modrinth Maven serves a classifier jar by its exact name <slug>-<version>-<classifier>.jar
    slug = settings("publish.modrinth_slug")
    downloads = []
    for asset in assets:
        target = asset
        if asset != primary:
            extra = pattern.match(asset)
            if not extra or not extra["classifier"]:
                fail(f"{asset} is not a classifier jar named {NAME_SHAPE}")
            if slug:
                target = f"{slug}-{version}-{extra['classifier']}.jar"
        downloads.append((asset, f"{args.dir}/{target}" if args.dir else target))
    files = [path for _, path in downloads]

    channel = args.version_type or "release"
    loaders_text = ", ".join(loaders)
    versions_text = " | ".join(game_versions)
    dependencies_text = ", ".join(dependencies) or "left to mc-publish"
    files_text = ", ".join(files)
    print(primary)
    print(f"  name:          {name}")
    print(f"  version:       {version} ({channel})")
    print(f"  loaders:       {loaders_text}")
    print(f"  game versions: {versions_text}  (filter: {game_filter})")
    print(f"  java:          {java or '-'}")
    print(f"  dependencies:  {dependencies_text}")
    print(f"  files:         {files_text}")
    summary(
        f"### {primary}\n"
        "| | |\n"
        "|---|---|\n"
        f"| Name | {name} |\n"
        f"| Version | `{version}` ({channel}) |\n"
        f"| Loaders | {loaders_text} |\n"
        f"| Game versions | `{versions_text}` (filter `{game_filter}`) |\n"
        f"| Java | {java or '-'} |\n"
        f"| Dependencies | {dependencies_text} |\n"
        f"| Files | {files_text} |"
    )
    output(name=name, version=version, loaders=loaders, game_versions=game_versions, java=java,
           dependencies=dependencies, filter=game_filter, files=files,
           downloads=[f"{asset} {path}" for asset, path in downloads])


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    commands = parser.add_subparsers(dest="command", required=True)

    p = commands.add_parser("config", help="print one setting of release.toml")
    p.add_argument("key")
    p.set_defaults(run=cmd_config)

    p = commands.add_parser("collect", help="copy the release jars into build/release-assets/")
    p.set_defaults(run=cmd_collect)

    p = commands.add_parser("matrix", help="publish matrix from the release assets")
    p.add_argument("assets", help="output of: gh release view <tag> --json assets")
    p.add_argument("--tag", required=True)
    p.add_argument("--versions", help="version table of the release tag, checks [targets] against the assets")
    p.add_argument("--only", help="publish only main jars matching this pattern")
    p.set_defaults(run=cmd_matrix)

    p = commands.add_parser("meta", help="mc-publish inputs of one release jar")
    p.add_argument("files", nargs="+", help="the main jar, then its classifier jars")
    p.add_argument("--versions", required=True, help="version table of the release tag")
    p.add_argument("--version-type", help="release | beta | alpha, shown in the summary")
    p.add_argument("--dir", help="directory the jars are downloaded to, prefixed to the files output")
    p.set_defaults(run=cmd_meta)

    args = parser.parse_args()
    args.run(args)


if __name__ == "__main__":
    main()
