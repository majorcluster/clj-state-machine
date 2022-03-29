(ns integration_core
  (:require [clojure.test :refer :all]
            [clj-state-machine.core :as c.core]
            [clj-state-machine.db.config :as db.config])
  (:use clojure.pprint))

(def test-server (atom {:core {}}))

(defn setup []
  (let [core (:core @test-server)
        is-empty (empty? core)]
    (cond is-empty (do
                     (println "test server started")
                     (db.config/erase-db!)
                     (swap! test-server assoc :core (c.core/start))))))
