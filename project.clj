(defproject clj-state-machine "0.1.0-SNAPSHOT"
  :description "Clojure state machine microservice"
  :repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                    :creds :gpg}]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.route "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [org.slf4j/slf4j-simple "1.7.28"]

                 [prismatic/schema "1.1.12"]

                 [com.datomic/datomic-pro "1.0.6362"]

                 ;TODO: create a test/dev profile
                 [prismatic/schema-generators "0.1.3"]
                 [org.clojure/test.check "0.10.0-alpha3"]]
  :main ^{:skip-aot true} clj-state-machine.core)
