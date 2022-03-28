(ns clj-state-machine.routes.status
  (:require [clj-state-machine.routes.params :as r.params]
            [clj-state-machine.routes.utils :as r.utils])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-status
  [request]
  {:status 200 :body "Hello, world!"})

(defn post-status
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          body (r.params/validate-and-mop request crude-body mandatory-fields allowed-fields)]
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))