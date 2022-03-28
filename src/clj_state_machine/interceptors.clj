(ns clj-state-machine.interceptors
  (:require [clojure.data.json :as json]))

(def json-out
  {:name ::coerce-body
   :leave
   (fn [context]
     (let [response         (get context :response)
           body             (get response :body)
           updated-response (assoc response
                              :body (json/write-str body))]
       (assoc context :response updated-response)))})
