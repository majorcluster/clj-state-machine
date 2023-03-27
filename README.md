# clj-state-machine
An open source clojure state machine API with datomic DB.
Extra features like history, graphql support, customized validations, cloud usage through marketplaces, and so on,   
to come on `clj-state-machine on-prem`, stay tunned.

## Setup
* Install and run local `free-datomic`:
```shell
docker run -d -e ADMIN_PASSWORD="admin" -e DATOMIC_PASSWORD="datomic" -p 4334-4336:4334-4336 --name datomic-free akiel/datomic-free
```
* Install dependencies
* Edit resource env files

## Documentation
See API documentation [here](doc/intro.md)

## Usage

start dev server:
```shell
lein with-profile dev run
```

test:
```shell
lein with-profile test test
```

## License
[MIT](LICENSE.md).  
[Datomic Free License](https://www.datomic.com/datomic-free-edition-license.html).
