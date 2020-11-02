$Dirs =  "parser/", "timeunits/", "plotting/", "constants/", "swing/"

$DEST= "../parser";

javac -Xlint:unchecked -cp "/.;lib/*;" src/parser/*.java src/timeunits/*.java src/plotting/*.java src/constants/*.java  src/swing/*.java src/LoggerParser.java  

mkdir -f parser;

cp src/LoggerParser.class ./
cp src/plotting/PlotFiles.py parser/;

cd src;

Foreach ($d in $Dirs) {
  Foreach ($f in $(ls $d | grep .class)) {
    $splitted = $($f -split '(\s+)',5);
    $file=$(echo $splitted[8]);
    echo "copy $d$file";
    cp $d$file $DEST;

  }
}
cd ..;
