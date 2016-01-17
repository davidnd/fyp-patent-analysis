#!/bin/sh
 echo "Compiling...."
find . -type f -name "*.java" > temp.txt
javac -cp ".:../jars/*" -d ../build @temp.txt
echo "Done!"