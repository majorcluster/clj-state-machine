(ns integration_core
  (:require [clojure.test :refer :all]
            [clj-state-machine.core :as c.core]
            [clj-state-machine.db.config :as db.config]
            [pedestal-api-helper.params-helper :as p-helper]
            [clojure.data.json :as json])
  (:use clojure.pprint))

(def test-server (atom {:core nil}))

(defn setup []
  (let [core (:core @test-server)
        is-nil (nil? core)]
    (cond is-nil (do
                     (println "test server started")
                     (db.config/erase-db!)
                     (swap! test-server assoc :core (c.core/start))))))

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
