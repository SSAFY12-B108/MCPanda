---
Title: MCP Tool - amplitude
Source: https://raw.githubusercontent.com/amplitude/mcp-server-guide/main/README.md
---

<div align="center">
  <img src="amplitude-logo.svg" alt="Amplitude" width="100" height="100">
</div>

# Amplitude MCP Server

Analyze your product data, experiments, and user behavior using conversational AI—bringing the full power of Amplitude directly into your AI workflow.

## Overview

The Amplitude MCP (Model Context Protocol) server enables teams to analyze product data, experiments, and user behavior through natural language conversations with AI. Query your analytics, dashboards, experiments, and feature flags without leaving your AI environment.

> [!WARNING]
> 🚧 The Amplitude MCP server is under active development. Some functions and settings may not be available, and you may experience bugs or performance issues during this period. This feature continues to evolve and might be subject to rate limits.

## Features

- **Analytics Queries**: Ask questions about user behavior, conversion rates, and product metrics
- **Experiment Analysis**: Analyze A/B test results and feature flag performance
- **Dashboard Access**: Retrieve data from existing charts and dashboards
- **Session Replay Search**: Find and analyze user sessions matching specific criteria
- **Natural Language Interface**: No need to learn complex query syntax—just ask questions

## Server Endpoints

Choose the server that matches your organization's data residency requirements:

- **US Server (Default)**: `https://mcp.amplitude.com/mcp`
- **EU Server**: `https://mcp.eu.amplitude.com/mcp`

## Setup

### Prerequisites

- Access to an Amplitude organization
- An MCP-compatible AI client (Claude, Cursor, ChatGPT, etc.)
- Appropriate project permissions in Amplitude

### Authentication

The Amplitude MCP server uses OAuth 2.0 authentication and respects your existing Amplitude permissions. You'll only have access to the data and projects you can normally view in Amplitude.

### Client Configuration

The setup process varies by AI client, but generally follows these steps:

1. **Add the MCP server** to your AI client configuration
2. **Use the appropriate server URL** for your region
3. **Complete OAuth authentication** when prompted
4. **Start querying** your Amplitude data

Consult your AI client's documentation for specific MCP server configuration instructions.

## Available Tools

The Amplitude MCP server provides these tools for analyzing your data:

### Core Analytics
- `search` - Find dashboards, charts, notebooks, and experiments
- `query_chart` - Retrieve data from specific charts
- `query_experiment` - Analyze experiment results and statistical significance
- `query_dataset` - Execute custom analytics queries
- `query_metric` - Get data for specific metrics

### Content Management
- `get_charts` - Retrieve full chart definitions
- `get_dashboard` - Access dashboard content and layout
- `get_notebook` - Fetch notebook content with charts and analysis
- `get_experiments` - Get detailed experiment information
- `get_flags` - Retrieve feature flag configurations

### User Insights
- `get_session_replays` - Search and filter session recordings
- `get_event_properties` - Explore event property definitions
- `get_context` - Access user and organization information

## Example Queries

Here are some examples of what you can ask the Amplitude MCP server:

**User Behavior Analysis**
- "What were the daily active users over the last 7 days?"
- "Show me the conversion funnel for our signup process"
- "Which features have the highest engagement rates?"

**Experiment Analysis**
- "What are the results of our homepage redesign experiment?"
- "Show me the statistical significance of the checkout flow test"
- "Which experiment variants are performing best?"

**Product Insights**
- "What are the top events by volume this week?"
- "Show me user retention by acquisition channel"
- "How do conversion rates differ across traffic sources?"

## Typical Analysis Workflow

1. **Search** for relevant content using natural language
2. **Retrieve** chart definitions and experiment details
3. **Query** data with custom parameters and filters
4. **Analyze** results and dive deeper with follow-up questions

## Best Practices

### Effective Prompting
- **Be specific** about time ranges, user segments, and metrics
- **Provide context** about what you're trying to understand
- **Break complex analyses** into smaller, focused questions
- **Reference existing content** when building on previous analyses

### Security Considerations
- Data access is limited to your current Amplitude permissions
- Third-party AI models process your data during analysis
- Review your organization's policies regarding AI data processing
- Organization administrators can control MCP server access

## Troubleshooting

### Common Issues
- **Authentication failures**: Verify your Amplitude account permissions
- **No data returned**: Check that you have access to the requested projects
- **Connection errors**: Ensure you're using the correct server URL for your region
- **Client issues**: Try restarting your AI client or reconfiguring the MCP server

### Getting Help
- Start with simple queries to test your connection
- Verify project access in the Amplitude web interface
- Check with your organization administrator about MCP server permissions

## Support

For technical support and questions about the Amplitude MCP server:

- **Documentation**: [Amplitude MCP Documentation](https://amplitude.com/docs/analytics/amplitude-mcp)
- **Feedback**: [Submit feedback on the MCP server](https://docs.google.com/forms/d/e/1FAIpQLSeFgRd8607Y2Gzidva5ChEri2tk7wvl7vofUIwxcM_2aD2Nqw/viewform)
- **Community**: [Amplitude Community](https://community.amplitude.com/)
- **Support**: [Amplitude Support](http://support.amplitude.com/)

---

*The Amplitude MCP server is currently in beta. Features and capabilities are continuously being improved based on user feedback.*
