# springboot3-components-starter

A powerful starter, the gateway to microservices!

The starter component provides the runtime support required for standard microservices, including the following major categories:

1. HTTP Server
2. RPC Server
3. RPC Client
4. Runtime Environment

The starter does not include trace components by default, does not include logging components by default.
Please import other components as needed.

## Usage Notes

If you need to develop microservices, especially single-module applications, feel free to import this component.
If your service has multiple modules, please do not import the starter in modules other than the entry application.
