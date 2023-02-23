(ns clj-state-machine.ports.http-in.routes.utils
  (:require [clj-state-machine.controllers.utils :as c.utils]))

(defn message-catch
  [_ e]
  (let [data (.getData e)
        type (get data :type :none)
        validation-messages (get data :validation-messages [])]
    (c.utils/common-with-validation-messages type validation-messages)))
