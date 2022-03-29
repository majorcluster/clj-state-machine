# clj-state-machine

### A clojure pedestal service designed to be an example of a simple web clojure project. Having tests, a simple crud, datomic integration

The whole setup for unit and integration tests are done and working

The db.entity file contains useful methods, nominally:
* find-by-id => for any entity finding it by id, not pulling refs
* upsert! => doing either insert or update based on find-by-id, 
it is concurrency proof with :db/cas for updates

controller.utils contains useful methods:
* redefine-keys => to replace {:id something} to {:status/id something} 
to match datomic usage
* undefine-keys to replace datomic keyword pattern, removing the entity
as prefix
* methods to build messages replacing values
* is-uuid to check if string matches an uuid pattern

routes.params has also handy methods:
* validate-and-mop => based on 2 arrays (mandatory & allowed) it validates
if request has mandatory params, then removes from map the unwanted attrs
avoiding any unwanted data being forwarded, 
uuid strings are right way converted to #uuid

## Setup
* Install local datomic / configure cloud one and get connection info to bring
* Run datomic (if local)
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
