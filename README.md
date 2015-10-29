Http server module for W4 BPMN+
===============================

Summary
-------

This is an addon module for W4 BPMN+ (v9.2+) that allows to embed an HTTP server inside the engine. 


Download
--------

The binary package is available from the [release page](https://github.com/w4software/w4-bpmnplus-module-httpd/releases)


Installation
------------

Extract the package, either zip or tar.gz, at the root of a W4 BPMN+ Engine installation. It will create the necessary entries into `modules` subdirectory of W4 BPMN+ Engine.


Configuration
-------------

Configuration of this module is done in the core configuration file of W4 BPMN+ (W4BPMNPLUS_HOME/conf/w4.properties).

Two properties need to be configured

- `module.http.port`: the port to listen on
- `module.http.root`: root directory of files to serve

Example

    module.http.port=7780
    module.http.root=$softwareHome/www


Usage
-----

When deployed, the module is started automatically by W4 BPMN+ Engine during its own start cycle.


License
-------

Copyright (c) 2015, W4 Software

This project is licensed under the terms of the MIT License (see LICENSE file)

Ce projet est licencié sous les termes de la licence MIT (voir le fichier LICENSE)
