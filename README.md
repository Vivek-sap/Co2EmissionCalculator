# [ Co2EmissionCalculator ]

Co2EmisionCalculator tool suffice following usecase
- calculate co2 consumption between two cities.

### Technology Used

* Spring boot
* Rest  
* lombok
* Java 8/11 
* openrouteservice

### Prerequisite
* should have maven install
* run on linux(any)/mac, Window not supported unless with git bash
* java 8 or more installed

#### Run and Compile
There are two shell script provided with following usage

- setup: for building and deploying the java artifact. 
- co2-calculator: for Co2 calculation 

```
sh setup.sh

```
* Manually

```
mvn clean install

java -jar <jar file>

```

#### Run Usage
* Example

```
./co2-calculator --start Hamburg --end Berlin --transportation-method medium-diesel-car
```

Input can be in any order

### Stop Java Process
```
netstat -nlp | grep 8080   OR  lsof -i :8080 -t

Kill -9 <process-id>
```

### Improvement
Code Quality and Integration Test

