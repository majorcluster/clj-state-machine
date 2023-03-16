(ns clj-state-machine.ports.http-in.routes.utils
  (:require [clj-state-machine.controllers.utils :as controllers.utils]))

(defn message-catch
  [_ e]
  (let [data (.getData e)
        type (get data :type :none)
        message (get data :message)
        validation-messages (get data :validation-messages [])]
    (if message (controllers.utils/common-with-custom-message type message)
        (controllers.utils/common-with-validation-messages type validation-messages))))
