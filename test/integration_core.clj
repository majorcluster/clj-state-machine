(ns integration_core
  (:require [clojure.test :refer :all]
            [clj-state-machine.core :as c.core]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.model.utils :as m.utils]
            [clojure.data.json :as json])
  (:use clojure.pprint)
  (:import (java.util UUID)))

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
      (m.utils/uuid-as-string)))
