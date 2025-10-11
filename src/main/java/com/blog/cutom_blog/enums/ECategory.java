package com.blog.cutom_blog.enums;

public enum ECategory {
    DEFI("DeFi", "Explore decentralized finance protocols, liquidity pools, yield farming, and the future of open finance."),
    NFTS("NFTs", "Discover digital ownership, NFT marketplaces, digital art, and the evolution of tokenized assets."),
    BLOCKCHAIN("Blockchain", "Learn about blockchain infrastructure, consensus mechanisms, and scaling solutions."),
    TRADING("Trading", "Master crypto trading strategies, technical analysis, and risk management."),
    SECURITY("Security", "Stay protected with best practices, security audits, and wallet safety tips."),
    WEB3("Web3", "Build the decentralized web with dApp development, smart contracts, and Web3 tools.");

    private final String displayName;
    private final String description;

    ECategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}