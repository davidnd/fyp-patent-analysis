#!/bin/sh
echo "killing old process"
kill $(ps aux | grep -E 'java.*fyp')
echo "Running classifier...."
(java -cp .:../jars/* fyp/classifier/Main) &
echo "Done!"