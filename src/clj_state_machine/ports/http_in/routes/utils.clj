(ns clj-state-machine.ports.http-in.routes.utils
  (:require [clj-state-machine.controllers.utils :as controllers.utils]))

(defn message-catch
  [_ e]
  (let [data (.getData e)
        type (get data :type :none)
        validation-messages (get data :validation-messages [])]
    (controllers.utils/common-with-validation-messages type validation-messages)))
