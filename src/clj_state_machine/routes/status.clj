(ns clj-state-machine.routes.status
  (:require [clj-state-machine.routes.params :as r.params]
            [clj-state-machine.routes.utils :as r.utils]
            [clj-state-machine.controller.status :as c.status]
            [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.config :as config])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-status
  [request]
  (let [params (get request :path-params)
        language (config/get-language request)
        id (get params :status-id)
        found (c.status/get-facade id)]
    (cond found {:status 200
                 :headers c.utils/headers
                 :body {:message ""
                        :payload found}}
          :else (c.utils/not-found-message language "status" "id") ))
  )

(defn post-status
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          body (r.params/validate-and-mop request crude-body mandatory-fields allowed-fields)]
      (c.status/insert-facade body)
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))