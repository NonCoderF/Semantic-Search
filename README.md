# Semantic Search Engine in Kotlin

A lightweight semantic search engine built completely from scratch in Kotlin using:

- Dynamic co-occurrence learning
- Relationship graph generation
- Automatic embedding creation
- Cosine similarity ranking

This project demonstrates the core principles behind modern semantic retrieval systems without using any AI/ML libraries.

---

# Features

✅ Dynamic vocabulary learning  
✅ Automatic word relationship discovery  
✅ Vector embedding generation  
✅ Sentence embeddings  
✅ Cosine similarity search  
✅ Distance-based relationship weighting  
✅ Fully written in pure Kotlin  
✅ No external NLP libraries  

---

# Problem Statement

Traditional keyword search fails to understand meaning.

Example:

Query:
```text
android ui toolkit
```

Document:
```text
Jetpack Compose framework
```

A normal keyword search may fail because the exact words do not match.

This project attempts to solve that using semantic relationships between words.

---

# Core Idea

Words appearing near each other likely share meaning.

Example:

```text
android kotlin compose mobile
```

The engine learns relationships like:

```text
android ↔ kotlin
android ↔ compose
compose ↔ ui
```

These relationships are converted into vector embeddings.

Documents and queries are then compared using cosine similarity.

---

# Architecture

```text
Documents
   ↓
Tokenizer
   ↓
Co-occurrence Graph Builder
   ↓
Dynamic Word Vector Generator
   ↓
Sentence Embedding Generator
   ↓
Cosine Similarity Search
   ↓
Ranked Results
```

---

# How It Works

## 1. Build Semantic Graph

The engine scans documents and builds relationships between nearby words.

Example:

```text
android -> kotlin, compose, mobile
```

Relationship strength increases based on:
- frequency
- proximity

Closer words receive higher weight.

---

## 2. Generate Word Embeddings

Each word becomes a vector.

Each dimension represents relationship strength with another vocabulary word.

Example:

```text
android = [0.0, 2.5, 1.2, 0.8, ...]
```

---

## 3. Generate Sentence Embeddings

Sentence vector =
average of all word vectors inside the sentence.

---

## 4. Semantic Search

The engine compares:
- query vector
- document vectors

using cosine similarity.

Higher similarity =
closer semantic meaning.

---

# Example

## Documents

```text
android kotlin compose ui mobile
stock market trading finance
clean architecture mvi android
```

## Query

```text
android ui toolkit
```

## Output

```text
android kotlin compose ui mobile -> 0.992
clean architecture mvi android -> 0.841
stock market trading finance -> 0.102
```

---

# Technologies Used

- Kotlin
- JVM
- Pure data structures
- Vector mathematics

No external machine learning libraries are used.

---

# Key Concepts Implemented

## Co-occurrence Learning

Words appearing together develop semantic relationships.

---

## Dynamic Embeddings

Embeddings are generated automatically from data.

No hardcoded vectors.

---

## Vector Space Representation

Words and sentences exist inside a mathematical vector space.

---

## Cosine Similarity

Measures semantic closeness between vectors.

Formula:

```text
similarity = A · B / (|A| × |B|)
```

---

# Project Structure

```text
src/
 ├── tokenizer/
 ├── graph/
 ├── embedding/
 ├── search/
 ├── similarity/
 └── main/
```

---

# Limitations

This project is intended as an educational semantic retrieval engine and does not yet implement:

- Neural training
- Word2Vec
- Transformers
- ANN indexing
- Contextual embeddings
- Dense vector optimization
- Large-scale pretraining

Current embeddings are sparse and generated from local document relationships.

---

# Future Improvements

## TF-IDF Weighting
Improve importance scoring for rare words.

---

## Dense Embeddings
Compress sparse vectors into lower-dimensional representations.

---

## Incremental Learning
Update relationships dynamically over time.

---

## Approximate Nearest Neighbor Search
Enable large-scale fast retrieval.

---

## Transformer Integration
Replace graph embeddings with neural embeddings.

---

# Why This Project Exists

The goal of this project is to understand how semantic meaning can emerge mathematically from textual relationships.

Instead of relying on external AI APIs, this project explores the foundations of:

- semantic search
- vector embeddings
- information retrieval
- NLP systems

from first principles.

---

# Inspiration

This project is conceptually inspired by:
- Co-occurrence matrices
- TF-IDF
- Word2Vec
- Early NLP systems
- Vector databases

---

# Author

Nizamuddin

Senior Android Developer  
Kotlin Enthusiast  
Interested in semantic systems, architecture, and AI-powered retrieval engines.
