# Set Maven binary
MVN=/usr/bin/mvn

# Parameters
releaseVersion=$1
developmentVersion=$2
releaseBranch=$3

if [[ "$1" == "" || "$2" == "" || "$3" == "" ]]; then
	echo ""
    echo "Please provide the release version, the developmentVersion and the release branch"
    echo ""
    echo "Usage: release.sh <releaseVersion> <developmentVersion> <releaseBranch>"
    echo "Example: release.sh 0.1.0 0.2.0-SNAPSHOT develop"
    echo ""
    exit 1
fi


# Set release version and build release package
git checkout $releaseBranch

echo "--------------"
echo "Set release version: $releaseVersion and build release..."
echo "--------------"

$MVN versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false
$MVN clean install

# Tag and commit version
echo "--------------"
echo "Tagging release version..."
echo "--------------"

git commit -a -m "Set release version to $releaseVersion"
git tag -a $releaseVersion -m "Release $releaseVersion"

# Do back merge to master
echo "--------------"
echo "Merging to master now..."
echo "--------------"

git fetch
git checkout master
git merge --no-ff -X theirs -m "Merge release branch with version $releaseVersion to master" $releaseBranch

# Set next development version, build, deploy
echo "--------------"
echo "Set next development version: $developmentVersion and build development version..."
echo "--------------"

git checkout $releaseBranch
$MVN versions:set -DnewVersion=$developmentVersion -DgenerateBackupPoms=false
$MVN -U clean install

git commit -a -m "Updated poms with the next development version $developmentVersion"

# Push all branches and tags
echo "--------------"
echo "Push manually all the branches and tags with:"
echo "> git push --all"
echo "> git push --tags"
echo "--------------"
