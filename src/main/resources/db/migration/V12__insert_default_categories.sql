-- Insert default blog categories
INSERT INTO categories (id, created_at, updated_at, name, description, slug) VALUES
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'DeFi', 'Explore decentralized finance protocols, liquidity pools, yield farming, and the future of open finance.', 'defi'),
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'NFTs', 'Discover digital ownership, NFT marketplaces, digital art, and the evolution of tokenized assets.', 'nfts'),
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'Blockchain', 'Learn about blockchain infrastructure, consensus mechanisms, and scaling solutions.', 'blockchain'),
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'Trading', 'Master crypto trading strategies, technical analysis, and risk management.', 'trading'),
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'Security', 'Stay protected with best practices, security audits, and wallet safety tips.', 'security'),
    (gen_random_uuid()::VARCHAR, NOW(), NOW(), 'Web3', 'Build the decentralized web with dApp development, smart contracts, and Web3 tools.', 'web3')
ON CONFLICT (name) DO NOTHING;