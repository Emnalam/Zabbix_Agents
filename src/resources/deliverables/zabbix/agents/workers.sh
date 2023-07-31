false_positives_script=/home/nwaagent/localbin/check_false_positives.sh
outputs_directory=/home/nwaagent/outputs
inputs_directory=/home/nwaagent/inputs
rules_directory=/home/nwaagent/agents/dat

$false_positives_script "${outputs_directory}/cim-banque" "${inputs_directory}/cim-banque/temp" "${rules_directory}/false_positives_rules_cim-banque.txt" "${inputs_directory}/cim-banque/false_positives" "${inputs_directory}/cim-banque"

$false_positives_script "${outputs_directory}/waverton-johprd" "${inputs_directory}/waverton-johprd/temp" "${rules_directory}/false_positives_rules_wav_johprd.txt" "${inputs_directory}/waverton-johprd/false_positives" "${inputs_directory}/waverton-johprd"

$false_positives_script "${outputs_directory}/waverton-dbjohprd" "${inputs_directory}/waverton-dbjohprd/temp" "${rules_directory}/false_positives_wav_johprd.txt" "${inputs_directory}/waverton-dbjohprd/false_positives" "${inputs_directory}/waverton-dbjohprd"

$false_positives_script "${outputs_directory}/bendura_prod" "${inputs_directory}/bendura_prod/temp" "${rules_directory}/false_positives_rules_bendura.txt" "${inputs_directory}/bendura_prod/false_positives" "${inputs_directory}/bendura_prod"


$false_positives_script "${outputs_directory}/bendura_uat" "${inputs_directory}/bendura_uat/temp" "${rules_directory}/false_positives_rules_bendura.txt" "${inputs_directory}/bendura_uat/false_positives" "${inputs_directory}/bendura_uat"


$false_positives_script "${outputs_directory}/oddo-bhf" "${inputs_directory}/oddo-bhf/temp" "${rules_directory}/false_positives_rules_oddo-bhf.txt" "${inputs_directory}/oddo-bhf/false_positives" "${inputs_directory}/oddo-bhf"


$false_positives_script "${outputs_directory}/mason-pblag" "${inputs_directory}/mason-pblag/temp" "${rules_directory}/false_positives_rules_mason.txt" "${inputs_directory}/mason-pblag/false_positives" "${inputs_directory}/mason-pblag"


$false_positives_script "${outputs_directory}/hottinger-ag" "${inputs_directory}/hottinger-ag/temp" "${rules_directory}/false_positives_rules_hottinger.txt" "${inputs_directory}/hottinger-ag/false_positives" "${inputs_directory}/hottinger-ag"


$false_positives_script "${outputs_directory}/tellco-ag" "${inputs_directory}/tellco-ag/temp" "${rules_directory}/false_positives_rules_tellco.txt" "${inputs_directory}/tellco-ag/false_positives" "${inputs_directory}/tellco-ag"

/usr/lib/zabbix/externalscripts/trap.sh 2>&1

find ${inputs_directory}/cim-banque -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;


find ${inputs_directory}/oddo-bhf -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;


find ${inputs_directory}/hottinger-ag -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;


find ${inputs_directory}/tellco-ag -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;


find ${inputs_directory}/mason-pblag -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;

find ${inputs_directory}/bendura_prod -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;

find ${inputs_directory}/bendura_uat -maxdepth 2 -mmin +10 -type f -exec rm -fv {} \;
