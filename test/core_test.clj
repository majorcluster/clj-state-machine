(ns core-test
  (:require [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.http-in.core :as service]
            [clojure.data.json :as json]
            [clojure.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [pedestal-api-helper.params-helper :as p-helper]))

(def base-message
  {:message ""
   :payload {}})

(def json-header
  {"Content-Type" "application/json"})

(defn map-as-json
  [map]
  (json/write-str map))

(defn json-as-map
  [json-str]
  (json/read-str json-str :key-fn keyword))

(defn id-as-string
  [entity id-ks]
  (-> entity
      id-ks
      (p-helper/uuid-as-string)))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(defn setup
  []
  (datomic.core/start-db))

(defn teardown
  []
  (datomic.core/erase-db!))

(defn test-fixture [f]
  (setup)
  (f)
  (teardown))
