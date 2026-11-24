## Regras de Arquitetura do Repositório
- O diretório `[caminho/do/backend, ex: /backend ou /server]` está CONGELADO. Nunca modifique, delete ou crie arquivos dentro dele.
- Todo desenvolvimento atual é estritamente focado na camada de apresentação (`[caminho/do/frontend, ex: /frontend ou /src/views]`).
- Qualquer chamada de dados deve respeitar rigorosamente os contratos já implementados nos arquivos de serviço existentes.