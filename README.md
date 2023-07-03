# LZW Compressor

Repositório em Java que disponibiliza o código do compressor LZW

## Pré-requisitos

- [JVM](https://www.java.com/pt-BR/download/manual.jsp)

## Executando o sistema pela primeira vez

Vá para o diretório BinaryLZW:

```sh
cd BinaryLZW/
```
Compile os arquivos .java:
```sh
javac *.java
```

## Compressão

Comando para comprimir um arquivo: java BinaryLZWCompression [arquivo_de_entrada.extensão] [compressed.lzw]

```sh
java BinaryLZWCompression input.jpeg compressed.lzw
```

## Descompressão

Comando para descomprimir um arquivo: java BinaryLZWDecompression [compressed.lzw][arquivo_de_sapida.extensão] 

```sh
java BinaryLZWDecompression compressed.lzw output.jpeg

```
