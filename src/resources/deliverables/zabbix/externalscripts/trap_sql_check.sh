#!/bin/bash

xmlroot="<sqlquerytotextresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

#echo ${ftp_file_pattern}

for f in ${ftp_file_pattern}; do
	resultsText=$(grep -Pzo '<sqlQueryResult>(\n|.)*(?=</sqlQueryResult>)' $f)
	countResults=$(sed -n '\|<count>|{:n;\|</count>|!{N;bn};y|\n| |;p}' $f)
	statusText=$(sed -n '\|<status>|{:n;\|</status>|!{N;bn};y|\n| |;p}' $f)	
	subProduct=$(sed -n '\|<subproduct>|{:n;\|</subproduct>|!{N;bn};y|\n| |;p}' $f)
	environment=$(sed -n '\|<environment>|{:n;\|</environment>|!{N;bn};y|\n| |;p}' $f)

	subProduct=${subProduct//<\/subproduct>/}
	subProduct=${subProduct//<subproduct>/}
	statusText=${statusText//<status>/}
	statusText=${statusText//<\/status>/}

	resultsText=${resultsText//<sqlQueryResult>/}

	statusText=${statusText// /}
	subProduct=${subProduct// /}

	statusColor=""

	if [ "$statusText" = "OK" ]; then
			statusColor="green"
	fi
	if [ "$statusText" = "NOK" ]; then
			statusColor="pink"
	fi
	if [ "$statusText" = "INFO" ]; then
			statusColor="blue"
	fi
	if [ "$statusText" = "UNKNOWN" ]; then
			statusColor="grey"
	fi


	if [ "$subProduct" = "oracle_apsys_svcs" ]; then
 		if [[ "$resultsText" =~ .+count=1[5-9] || "$resultsText" =~ .+count=[1-9]{3,} || "$resultsText" =~ .+count=[2-9][0-9] ]]; then
			 statusColor="lightyellow"
			 statusText="WARNING"
	 	else	
			if [[ "$resultsText" =~ .+count=2[6-9] || "$resultsText" =~ .+count=[1-9]{3,} || "$resultsText" =~ .+count=[3-9][0-9] ]]; then
                        	 statusColor="red"
                         	statusText="PROBLEM"
              		else
				statusColor="green"
                                statusText="OK"	
			fi 
		 fi
	
	fi 


	ECHO="<html>"
	ECHO="$ECHO <style>"
  ECHO="$ECHO			pre {"
	ECHO="$ECHO	  		display: block;"
	ECHO="$ECHO	   		font-family: monospace;"
	ECHO="$ECHO	  		white-space: pre;"
  ECHO="$ECHO		  	margin: 1em 0; }"
	ECHO="$ECHO			.sql-modal {
  									display: none; /* Hidden by default */
									  position: fixed;  /*Stay in place */
									  z-index: 1; /* Sit on top */
									  left: 0;
									  top: 0;
									  width: 100%; /* Full width */
									  height: 100%; /* Full height */
									  overflow: auto; /* Enable scroll if needed */
									  background-color: rgb(0,0,0); /* Fallback color */
										background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
									}

									/* Modal Content/Box */
									.sql-modal-content {
									  background-color: yellow;
									  margin: 15% auto; /* 15% from the top and centered */
									  padding: 20px;
									  border: 1px solid #888;
										color:black;
									  width: 95%;  Could be more or less, depending on screen size */
									}
									/* The Close Button */
.sql-close {
  color: #aaaaaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.sql-close:hover,
.sql-close:focus {
  color: #000;
  text-decoration: none;
  cursor: pointer;
}

													</style>"
													
  ECHO="$ECHO <!--"
  ECHO="$ECHO  Status:$statusText"
  ECHO="$ECHO -->
									<div id=\"myModal_$subProduct\" class=\"sql-modal\">
									<!-- Modal content -->
									<div id=\"content_$subProduct\" class=\"sql-modal-content\">
									<span id=\"close_$subProduct\" class=\"sql-close\">&times;</span>
										<p><h2>$subProduct</h2><pre>$resultsText</pre></p>
									</div>

									</div>"

	ECHO="$ECHO <table>"
	ECHO="$ECHO <tr><td>Status</td></tr>"
	ECHO="$ECHO <tr><td bgcolor=\"$statusColor\" align=\"center\" style=\"color:black\">$statusText</td></tr>"
	ECHO="$ECHO <tr><td>
								<button id=\"myBtn_$subProduct\" style=\"background-color:transparent;color:grey\">View data</button>
								</td>
							</tr>"
  ECHO="$ECHO </table>
	
	 <script>
// Get the modal
var modal_$subProduct = document.getElementById(\"myModal_$subProduct\");

// Get the <span> element that closes the modal
var span_$subProduct = document.getElementById(\"close_$subProduct\");

// Get the button that opens the modal
var btn_$subProduct = document.getElementById(\"myBtn_$subProduct\");

// When the user clicks the button, open the modal
btn_$subProduct.onclick = function() {
  modal_$subProduct.style.display = \"inline-block\";
}

// When the user clicks on <span> (x), close the modal
span_$subProduct.onclick = function() {
  modal_$subProduct.style.display = \"none\";
}

</script>"
  ECHO="$ECHO </html>"

	

  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.sql_check_$subProduct" -o "$ECHO"	
# rm $f
done
