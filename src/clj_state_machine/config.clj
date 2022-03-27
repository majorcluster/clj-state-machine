(ns clj-state-machine.config)

(def cookies
  {:language "csm_language"})

;TODO implement cookies logic
(defn get-language
  [request]
  (let [default :en]
    default))