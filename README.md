# clj-state-machine

### A clojure pedestal service designed to be an example of a simple web clojure project. Having tests, a simple crud, datomic integration

The whole setup for unit and integration tests are done and working

controller.utils contains useful methods:
* redefine-keys => to replace {:id something} to {:status/id something} 
to match datomic usage
* undefine-keys to replace datomic keyword pattern, removing the entity
as prefix
* methods to build messages replacing values

## Setup
* Install and run local free-datomic:
```shell
docker run -d -e ADMIN_PASSWORD="admin" -e DATOMIC_PASSWORD="datomic" -p 4334-4336:4334-4336 --name datomic-free akiel/datomic-free
```
* Install dependencies
* Copy and paste .profiles.clj without the "." at the beginning
* Configure it with test and dev connection info

## Usages

start dev server:
```shell
lein run
```

test:
```shell
lein test
```
