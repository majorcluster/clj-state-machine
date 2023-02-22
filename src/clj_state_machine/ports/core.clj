(ns clj-state-machine.ports.core
  (:require [clj-state-machine.ports.datomic.core :as d.c]
            [clj-state-machine.ports.http-in.core :as http-in.c]))

(defn start-ports-dev
  []
  (d.c/start-db)
  (http-in.c/start-dev))

(defn start-ports
  []
  (d.c/start-db)
  (http-in.c/start))
