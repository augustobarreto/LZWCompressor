# LZW Compressor

Repositório em Java que disponibiliza o código do compressor LZW

## Pré-requisitos

- [JVM](https://www.java.com/pt-BR/download/manual.jsp)

## Executando o sistema pela primeira vez

Vá para o diretório desejado:

- LZW: Apenas para arquivos de texto

- BinaryLZW: Todo tipo de arquivo

```sh
javac *.java
```

## Compressão

No BinaryLZW, por exemplo, teríamos:

```sh
java BinaryLZWCompression input.jpeg compressed.lzw
```

## Descompressão

```sh
java BinaryLZWDecompression compressed.lzw output.jpeg

```
