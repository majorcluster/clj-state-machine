(ns clj-state-machine.routes.utils
  (:require [clj-state-machine.controller.utils :as c.utils])
  (:use clojure.pprint))

(defn message-catch
  [_ e]
  (let [data (.getData e)
        type (get data :type :none)
        validation-messages (get data :validation-messages [])]
    (c.utils/common-with-validation-messages type validation-messages)))


