#!/bin/bash -l

print_revs(){
    echo '# commit             date                 issue      message'
    echo
    git log $1 --pretty=format:'%h %aI %s' | sed -r -n 's/^([^ ]+) ([^ ]+) (.*fix(ed)? +#([0-9]+).*)/\1 \2 \5 \3/ip' | while read commit date issue message; do
        printf '  %s    %s    %4s    %s\n' "$commit" "$date" "$issue" "$message"
    done
}

since_release="$(sed -r -n 's#.*[<]cmdbuild.track_issues_since[>]([^<]+).*#\1#p' `dirname $0`/../../parent/pom.xml)"
since_tag="release/${since_release}"

echo
echo "# list of issues fixed with this release (previous release was $since_release)"
echo

print_revs "$(git rev-list -n 1 $since_tag)..HEAD"


since_date=$(date -d "-1 month" +%Y-%m-%d)

echo
echo "# list of issues fixed since $since_date (regardless of release)"
echo

print_revs --since=$since_date

echo
