(ns omniconf-demo.core
  (:require [omniconf.core :as cfg])
  (:gen-class))

(cfg/define
  {:hostname {:description "Where service is deployed"
              :type :string
              :required true
              :default "formcept005"}
   :port {:description "HTTP port"
          :type :number
          :default 8080}
   :conf {:description "Configuration file"
          :type :file
          :verifier omniconf.core/verify-file-exists}
   :log-level {:description "Describes the log levels"
               :one-of [:all :debug :error :fatal :info :warn]
               :default :all}
   :password {:description "Password for logging in"
              :type :string
              :secret true}})

;; Command line arguments

;; (defn -main
;;   "Omniconf Demo"
;;   [& args]
;;   (prn "Hostname before omniconf cmd" (cfg/get :hostname))
;;   (cfg/populate-from-cmd args)
;;   (prn "Hostname after omniconf cmd" (cfg/get :hostname)))


;; Environment variables

;; (defn -main
;;   "Omniconf Demo"
;;   [& args]
;;   (prn "Port before omniconf env" (cfg/get :port))
;;   (cfg/populate-from-env)
;;   (prn "Port after omniconf env" (cfg/get :port)))


;; Configuration file

;; (defn -main
;;   "Omniconf Demo"
;;   [& args]
;;   (prn "Hostname before omniconf file" (cfg/get :hostname))
;;   (cfg/populate-from-cmd args)
;;   (when-let [conf (cfg/get :conf)]
;;     (cfg/populate-from-file conf))
;;   (prn "Hostname after omniconf file" (cfg/get :hostname)))

;; Java properties

;; (defn -main
;;   "Omniconf Demo"
;;   [& args]
;;   (prn "Hostname before omniconf java" (cfg/get :hostname))
;;   (cfg/populate-from-properties)
;;   (prn "Hostname after omniconf java" (cfg/get :hostname)))

;; Putting it all together
;; (defn -main
;;   "Omniconf Demo"
;;   [& args]
;;   (cfg/populate-from-cmd args)
;;   (cfg/populate-from-properties)
;;   (when-let [conf (cfg/get :conf)]
;;     (cfg/populate-from-file conf))
;;   (cfg/populate-from-env)
;;   (cfg/verify :quit-on-error true))
