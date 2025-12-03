---
Title: MCP Tool - jenkins
Source: https://raw.githubusercontent.com/jasonkylelol/jenkins-mcp-server/main/README.md
---

# jenkins-mcp-server
mcp server for jenkins build tasks

# usage
1. config environment
```
vim .env # config jenkins url, user, token and openai api base url, model, api key.
```
make sure to edit server_config.json for your own mcp server port  

2. launch server
```
python3 mcp_server.py # modify to config your own mcp server port
```
3. run a jenkins build task
```
python3 mcp_client.py
```
then you will see the build task is running in jenkins, when the task is done, the client will show the result.
