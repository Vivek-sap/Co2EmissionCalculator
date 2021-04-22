#!/bin/bash

while [ $# -gt 0 ]; do
  case "$1" in
    --start=*)
      start="${1#*=}"
      ;;
    --end=*)
      end="${1#*=}"
      ;;
    --transportation-method=*)
      transportation="${1#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

if [[ -z $start || -z $end || -z $transportation ]];
then
    echo `date`" - Missing mandatory arguments: start, end and  transportation. "
    echo `date`" - Usage: --start [startCityName] --end [EndCityName] --transportation-method [methid-name] . "
    exit 1
fi

result=$(curl -s GET --header "Accept: */*" "http://localhost:8080/emission/v1/calculate/start/$start/end/$end/transportmethod/$transportation" | python -c 'import json,sys;obj=json.load(sys.stdin);print (obj[list(obj.keys())[0]])') 
echo $result
exit

