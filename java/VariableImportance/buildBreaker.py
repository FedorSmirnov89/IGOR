"""
Script to check the sonarqube status and return 0 or 1.

"""
import sys
import requests
from time import sleep


time = 1  # time in seconds

# wait for specified time
print ('waiting for quality gate calculation')
sleep(time)

# send request to quality gate api and read the status
print ('requesting status')
r = requests.get('https://codesigni182.informatik.uni-erlangen.de/sonarqube/api/qualitygates/project_status?projectKey=abo:IGOR')
print ('request returned')
json = r.json()
status = (json['projectStatus']['status'])

# return with appropriate code
if status == 'ERROR':
    print ('error status, breaking build')
    sys.exit(1)
else:
    sys.exit(0)

