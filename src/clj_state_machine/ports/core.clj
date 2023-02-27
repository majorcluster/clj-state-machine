(ns clj-state-machine.ports.core
  (:require [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.http-in.core :as http-in.core]))

(defn start-ports-dev
  []
  (datomic.core/start-db)
  (http-in.core/start-dev))

(defn start-ports
  []
  (datomic.core/start-db)
  (http-in.core/start))
