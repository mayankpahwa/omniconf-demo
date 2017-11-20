## Managing application configuration conveniently using Omniconf

Almost every program expects some kind of configurations which are usually spread across multiple sources. Keeping track of all of them, unifying them, verifying whether they exist and in their correct syntax can be really hard and error prone, especially if configurations are coming from a variety of sources.

There are a lot of configuration libraries written for Clojure and they are good at what they do. However, most of them do not include the verification part, which was a prime requirement for us. For the verification, omniconf provides the verify API and hence we chose Omniconf as our primary configuration library at Formcept.

Omniconf is built on the following principles:

1. Define in a single place, populate from many - Specify at a single place the type of configurations your application expects. However, populating the configurations shouldn’t be limited to one place. Your configurations can come from different sources ex: command line, environment variables, java properties, configuration file etc.

2. Access from anywhere in the application - Once you have defined your configurations, you should be able to access all your configurations from anywhere in your application. 

3. All configuration sources must be merged - It shouldn’t matter where the configuration is set from. It should be uniformly initialized, verified and accessed as Clojure data.

4. Verification before execution - This is to prevent errors occurring in the middle of your program. The whole configuration is checked before the main application is executed. If there is a problem, a helpful message is presented to the user. Ex: you have defined a file from which you will be reading some data. Somehow, you forget to create the file. If the you specify the file in configurations, omniconf will fail in the verification step even before code is executed.

### Important functions in omniconf

- omniconf.core/define - Defines the syntax of your configuration. Configuration syntax needs to be defined before setting the configuration values
- omniconf.core/get - Gets the value of a set configuration
- omniconf.core/verify - The verify function is used to check the defined configurations. It fails if any of the options do not conform to the provided syntax. So, you can catch errors even before your app is deployed. Ex: the file not found error as discussed above


### Code

Here is an example of using omniconf with a very basic setup.

Step 1: Require the omniconf core namespace in your demo namespace

```clojure
(ns omniconf-demo.core
  (:require [omniconf.core :as cfg])
  (:gen-class))
  ```

Step 2: Define the syntax of the configuration your application expects

```clojure
(cfg/define
  {:hostname {:description "Where service is deployed"
              :type :string
              :required true}
   :port {:description "HTTP port"
          :type :number
          :default 8080}
   :conf {:description "Configuration file"
          :type :file
          :verifier omniconf.core/verify-file-exists}
   :password {:description "Password for logging in"
              :type :string}})
```

Step 3: Set the values of your parameters

Let’s add the port number as an environment variable. Open your bashrc file and add a line at the bottom 
```export PORT=8888```

Now, let’s set the value of the password in the java properties. To set this, modify your project.clj file to have this line in your dev profile
```:jvm-opts ["-Dpassword=secret"]```

As can be seen in the syntax above, we have defined a :conf parameter of the type file. So, we’ll create a file named conf.edn and pass the :conf parameter to omniconf at run time giving the value as the file name. The content of the file conf.edn should be a clojure map. The current contents of the conf.edn file are:
```clojure
{:hostname "formceptfile"}
```

Step 4: Run the program

In your main namespace, edit the main function to:
```clojure
(defn -main
  "Omniconf Demo"
  [& args]
  (cfg/populate-from-cmd args)
  (cfg/populate-from-properties)
  (when-let [conf (cfg/get :conf)]
    (cfg/populate-from-file conf))
  (cfg/populate-from-env)
  (cfg/verify :quit-on-error true))
 ```
And run the program 
```clojure
lein run --conf conf.edn
```

The output produced by the run command is as follows:

```clojure
Omniconf configuration:
 {:conf #object[java.io.File 0x421bba99 "conf.edn"],
 :hostname "formceptfile",
 :password "secret",
 :port 8888}
 ```

As can be seen, the values of different configurations have been populated from different places. The :conf param was provided as a command line argument, :hostname was defined in the conf file conf.edn, :port was set as an environment variable and the value of :password was given as java property.

It is also worth noting that the order in which configuration are set is totally up to the user. Omniconf assigns priority to the sources in the same order in which it reads them. So, if you want your command line arguments to overwrite any of your other sources, read the command line arguments at the end.