#!/usr/bin/python3

# getProblems.py - Give back problems concerning one product of one client
#
# Usage: getProblems.py <client> <product>
#
# History:
# 20191114 - FRI     - First version

import argparse
import json
import requests

# Send request to Zabbix         
def post(url, data,id=''):
  headers = { 'Content-Type': 'application/json' }
  if id != '':
    data["auth"] = id
  return requests.post( url, 
                        headers=headers, 
                        data=json.dumps(data), 
                        verify=False
                      ).json()


parser = argparse.ArgumentParser()
parser.add_argument("-u", "--url", help="url to connect to zabbix: http://ch-gbc-zabbix/zabbix/api_jsonrpc.php")
parser.add_argument("-lu", "--username", help="username to authenticate on zabbix")
parser.add_argument("-lp", "--password", help="password to authenticate on zabbix")
parser.add_argument("-c", "--client", default="G2", help="Client to be considered")
parser.add_argument("-p", "--product", default="Apsys", help="Product to be tested")
parser.add_argument("-d", "--debug", action="store_true", help="Print retrieved problems")
args = parser.parse_args()

Client = args.client
Product = args.product
Host = Client + " - " + Product
Username = args.username
Password = args.password
URL = args.url

# Get back the authentification ID
getID={"jsonrpc": "2.0", "method": "user.login", "params": { "user": Username, "password": Password }, "id": 1}
ID = post(URL, getID)["result"]

# Récupération du Host ID
getHost={ "jsonrpc": "2.0", "method": "host.get", 
          "params": { 
            "filter": { 
              "host": [ 
                Host
              ] 
            }
          }, 
          "id": 1
        }
hostid = post(URL, getHost,ID)['result'][0]['hostid']
Problem = False
Warning = False

# We get back the Alerts list
getProblems={ "jsonrpc": "2.0", "method": "problem.get", 
              "params": { "output": "extend", 
                          "hostids": hostid,
                          "severities": [3,4,5],
                          "acknowledged": False
                        }, 
              "id": 1
          }
Problem = post(URL, getProblems,ID)['result']

if len(Problem) != 0:
  print("PROBLEM")
  if args.debug:
    print(Host)
    print(Problem)
else:
  getProblems={ "jsonrpc": "2.0", "method": "problem.get", 
                "params": { 
                  "output": "extend", 
                  "hostids": hostid,
                  "severities": 2,
                  "acknowledged": False
                }, 
                "id": 1
              }
  Warning = post(URL, getProblems,ID)['result']
  if len(Warning) != 0:
    print("WARNING")
    if args.debug:
      print(Host)
      print(Warning)
  else:
    print("OK")

logout={ "jsonrpc": "2.0", "method": "user.logout",
          "params": [] 
          ,
          "id": 1
        }
logout_result = post(URL, logout, ID)['result']
