#!/bin/bash

if $# < 2
then
	echo need to input commit message
	exit
fi

git add *

git commit -m "$1"

git push
