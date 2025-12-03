---
Title: MCP Tool - github-community
Source: https://raw.githubusercontent.com/0xshariq/github-mcp-server/main/README.md
---

# Github MCP Server

🔗 **[View on MCP Market](https://mcpmarket.com/server/github-git-assistant)**
<br />
🔗 **[View on MCP Registry](https://mcp.so/server/github-mcp-server/Sharique%20Chaudhary)**
<br />
📦 **[Available on npm](https://www.npmjs.com/package/@0xshariq/github-mcp-server)**

A **Model Context Protocol (MCP) server** that provides **29 Git operations + 11 workflow combinations** for AI assistants and developers. This server exposes comprehensive Git repository management through a standardized interface, enabling AI models and developers to safely manage complex version control workflows.

## 🚀 Quick Installation

### Option 1: Install from npm (Recommended)
```bash
# Install from npm
npm install -g @0xshariq/github-mcp-server

# Test the installation
gstatus
glist
```

### Option 2: Symbolic Links (Alternative - No Package Manager)
```bash
# Clone and setup
git clone https://github.com/0xshariq/github-mcp-server.git
cd github-mcp-server
npm install && npm run build

# Create symbolic links (cross-platform)
./setup-symbolic.sh --user          # User installation
# OR
sudo ./setup-symbolic.sh           # System-wide (Linux/macOS)

# Test the installation
gstatus
glist
```

> **🛠️ Having Issues?** If commands aren't working after installation, see our **[Complete Troubleshooting Guide](markdown/TROUBLESHOOTING.md)** for solutions to all common problems including "command not found" errors, path conflicts, PNPM issues, and the symbolic links alternative.

## 🎯 About

**GitHub MCP Server** bridges AI assistants with Git repositories and provides powerful developer productivity tools. It provides:

- **Safe Git operations** through a standardized MCP interface (29 operations)
- **Complete version control** capabilities including advanced operations (tag, merge, rebase, cherry-pick, blame, bisect)
- **31 workflow combinations** for enhanced developer productivity
- **Advanced developer tools** (backup, cleanup, workflow automation)
- **Error handling and validation** to prevent common Git mistakes
- **Direct integration** with VS Code and AI assistants like GitHub Copilot
- **CLI wrapper** for terminal access and automation

## 🚀 Features Overview

This server provides comprehensive Git repository management through two main categories:

### 📁 **Basic Git Operations** (17 operations)

Essential daily Git commands organized in [`bin/basic/`](bin/basic/) - see **[Basic Operations Guide](bin/basic/README.md)** for detailed documentation.

- **File Management**: Add, remove files from staging area
- **Repository Information**: Status, history, differences
- **Commit Operations**: Create commits, push, pull
- **Branch Management**: Create, switch branches
- **Remote Management**: Add, remove, configure remotes
- **Stash Operations**: Temporarily save changes
- **Reset Operations**: Repository state management

### 🚀 **Advanced Git Operations** (12 operations)

Sophisticated workflows and automation in [`bin/advanced/`](bin/advanced/) - see **[Advanced Workflows Guide](bin/advanced/README.md)** for comprehensive documentation.

- **Workflow Combinations**: Complete flows (add→commit→push), quick commits, sync operations
- **Development Tools**: Smart development workflows, backup systems
- **Advanced Git Features**: Tags, merging, rebasing, cherry-picking, blame, bisect
- **Maintenance & Safety**: Repository cleanup, optimization, backup management
- **Professional Workflows**: Release management, hotfix procedures, team collaboration

## 🛠️ Installation

**See [markdown/INSTALLATION.md](markdown/INSTALLATION.md)** for detailed installation guide for Windows, macOS, WSL, and all platforms.

## 🏗️ Project Structure & Architecture

**GitHub MCP Server** is organized for clarity and progressive learning:

```
github-mcp-server/
├── src/
│   ├── index.ts              # MCP server (29 tool registrations, schema definitions)
│   └── github.ts             # Git operations engine (all 29 implementations)
├── bin/
│   ├── basic/                # 📁 17 Essential Git Operations
│   │   ├── README.md         # Comprehensive basic operations guide
│   │   ├── gadd.js           # Add files (git add)
│   │   ├── gcommit.js        # Create commits (git commit)
│   │   ├── gpush.js          # Push changes (git push)
│   │   ├── gpull.js          # Pull changes (git pull)
│   │   ├── gstatus.js        # Repository status (git status)
│   │   ├── gbranch.js        # Branch management (git branch)
│   │   ├── gcheckout.js      # Branch switching (git checkout)
│   │   ├── glog.js           # Commit history (git log)
│   │   ├── gdiff.js          # Show differences (git diff)
│   │   ├── gstash.js         # Stash operations (git stash)
│   │   ├── gpop.js           # Apply stash (git stash pop)
│   │   ├── greset.js         # Reset operations (git reset)
│   │   ├── gclone.js         # Clone repositories (git clone)
│   │   ├── gremote.js        # Remote management (git remote)
│   │   └── ginit.js          # Initialize repository (git init)
│   └── advanced/             # 🚀 13 Advanced Workflows & Automation
│       ├── README.md         # Comprehensive advanced workflows guide
│       ├── gflow.js          # Complete workflow (add→commit→push)
│       ├── gquick.js         # Quick commit workflow
│       ├── gsync.js          # Sync workflow (pull→push)
│       ├── gdev.js           # Development session management
│       ├── gworkflow.js      # Professional workflow combinations
│       ├── gfix.js           # Smart fix and patch workflows
│       ├── gfresh.js         # Fresh start workflows
│       ├── gbackup.js        # Backup and safety operations
│       ├── gclean.js         # Repository cleanup and optimization
│       ├── gsave.js          # Save and preserve workflows
│       ├── glist.js          # Tool discovery and help system
│       ├── grelease.js       # Release management workflows
│       └── common.js         # Shared utilities and helpers
├── markdown/
│   ├── INSTALLATION.md      # Detailed installation guide
│   ├── DEPLOY.md            # Production deployment guide
│   ├── DOCKER.md            # Docker setup and deployment guide
│   └── QUICK_REFERENCES.md  # Copy-paste command reference
├── mcp-cli.js               # Enhanced CLI wrapper (organized by structure)
├── package.json             # Project configuration & npm scripts
├── tsconfig.json            # TypeScript configuration
└── README.md                # This comprehensive guide
```

## 📖 Documentation Structure

- **[bin/basic/README.md](bin/basic/README.md)** - Complete guide to 17 essential Git operations
- **[bin/advanced/README.md](bin/advanced/README.md)** - Comprehensive advanced workflows documentation
- **[markdown/INSTALLATION.md](markdown/INSTALLATION.md)** - Step-by-step installation for all platforms
- **[markdown/TROUBLESHOOTING.md](markdown/TROUBLESHOOTING.md)** - 🛠️ **Complete troubleshooting guide** - Solutions to all common issues
- **[markdown/MCP_UNIVERSAL_CONFIG.md](markdown/MCP_UNIVERSAL_CONFIG.md)** - Universal MCP configuration for all LLM clients
- **[markdown/QUICK_REFERENCES.md](markdown/QUICK_REFERENCES.md)** - Copy-paste commands for quick reference
- **[markdown/DOCKER.md](markdown/DOCKER.md)** - Docker setup, deployment, and containerization
- **[markdown/DEPLOY.md](markdown/DEPLOY.md)** - Production deployment and hosting strategies

## 🔧 Technical Architecture

### 📡 **MCP Server Core** (src/index.ts)

- **29 Tool Registrations** with complete JSON schemas
- **Enhanced Metadata** with operation tracking and performance monitoring
- **Input Validation** using Zod schemas for type safety
- **Error Handling Pipeline** with timeout protection and meaningful messages
- **Cross-platform Compatibility** with environment normalization

### ⚙️ **Git Operations Engine** (src/github.ts)

- **Comprehensive Implementation** of all 29 Git operations
- **Security Features** - Command injection prevention and input sanitization
- **Enhanced Error Handling** with context-aware messaging for common scenarios
- **Performance Monitoring** - Operation duration tracking and logging
- **Safety Checks** - Repository validation and file existence verification

### 🖥️ **Enhanced CLI System**

- **Smart Organization** - Tools categorized by basic vs advanced operations
- **Directory-Aware Help** - References to specific README files for detailed guidance
- **Progressive Learning** - Clear path from basic to advanced operations
- **Tool Discovery** - Enhanced `glist` command with category filtering

## 🛡️ Error Handling & Safety

- **🔍 Repository Validation**: Ensures directory is a valid Git repository
- **📁 File Existence Checks**: Validates files exist before Git operations
- **⏱️ Timeout Protection**: 30-second timeout for operations
- **🚫 Input Sanitization**: Prevents command injection
- **📝 Detailed Error Messages**: Clear, actionable error descriptions

## 🛠️ Troubleshooting

Having issues with installation or commands not working? We've got you covered! Our comprehensive troubleshooting guide covers solutions to all common problems:

### 🚨 Most Common Issues & Quick Fixes

| Problem | Quick Solution |
|---------|----------------|
| 💥 `command not found` errors | `unset command_not_found_handle` |
| 📁 Path conflicts with PNPM | Remove `/5/` from global directory |
| 🔗 Commands show help instead of executing | Update wrapper scripts to pass command names |
| 🚫 Permission denied | `chmod +x ~/.local/share/pnpm/g*` |

**👉 For detailed solutions, step-by-step fixes, and diagnostic tools, see our [Complete Troubleshooting Guide](markdown/TROUBLESHOOTING.md)**

### 🆘 Quick Diagnostic

```bash
# Test if commands are working
gstatus                    # Should show repository status
env gstatus               # Alternative if above fails
which gstatus             # Should show path to command

# If still having issues, see TROUBLESHOOTING.md for complete solutions
```

## License

ISC License

## Author

Created for use with AI assistants that support the Model Context Protocol.
