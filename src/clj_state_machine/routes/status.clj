(ns clj-state-machine.routes.status
  (:use clojure.pprint))

(defn get-status
  [request]
  {:status 200 :body "Hello, world!"})

(defn post-status
  [request]
  (let [params (:json-params request)]
    (pprint params)))