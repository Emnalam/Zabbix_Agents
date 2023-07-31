mkdir -p ${2}
mkdir -p ${4}

mv ${1}/* ${2}
false_positives=`grep -lnrf ${3} ${2}`

for file in $false_positives; do
				echo $file
				mv $file ${4}
done

for remaining in `ls -d ${2}/*`; do
				mv $remaining ${5}
done
