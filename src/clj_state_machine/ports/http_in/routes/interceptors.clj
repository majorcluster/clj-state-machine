(ns clj-state-machine.ports.http-in.routes.interceptors
  (:require [clojure.data.json :as cjson]))

(defn convert-to-json
  [m]
  (cond (map? m) (cjson/write-str m)
        :else m))

(defn json-out
  []
  {:name ::json-out
   :leave (fn [context]
            (->> (:response context)
                 :body
                 (convert-to-json)
                 (assoc-in context [:response :body])))})
