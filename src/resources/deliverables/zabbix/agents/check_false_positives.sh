start=`date +%s`
mkdir -p ${2}
mkdir -p ${4}
umask 000
mv -f ${1}/* ${2}
tempFiles=`ls -d ${2}/*`

false_positives=`grep -lnrf ${3} ${2} 2>/dev/null`

# Exclude False positive files
for file in $false_positives; do
       if [ -n $files ]; then 
        mv -f $file ${4}
       fi
done

# Move remaining files to Inputs directory
for remaining in $tempFiles; do
        mv -f $remaining ${5} 2>/dev/null
done
end=`date +%s`
runtime=$((end-start))
if [ $runtime -gt 0 ];then
 echo $start, $end, $((end-start)) >> /home/nwaagent/agents/dat/runtime.txt
fi
