(ns clj-state-machine.server
  (:gen-class) ; for -main method in uberjar
  (:require [clj-state-machine.ports.core :as ports.c]))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (ports.c/start-ports-dev))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (ports.c/start-ports))
