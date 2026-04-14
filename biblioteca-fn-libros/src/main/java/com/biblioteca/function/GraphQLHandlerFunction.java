package com.biblioteca.function;

import com.biblioteca.graphql.GraphQLProvider;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

import java.util.Map;
import java.util.Optional;

public class GraphQLHandlerFunction {

    private static final GraphQL graphQL = GraphQLProvider.buildGraphQL();

    @FunctionName("graphqlHandlerLibros")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "graphql"
        )
        HttpRequestMessage<Optional<Map<String, Object>>> request,
        final ExecutionContext context
    ) {
        Map<String, Object> body = request.getBody().orElse(null);

        if (body == null || !body.containsKey("query")) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(Map.of("error", "El body debe incluir la propiedad 'query'"))
                .build();
        }

        String query = String.valueOf(body.get("query"));
        Object variables = body.get("variables");

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables instanceof Map ? (Map<String, Object>) variables : Map.of())
            .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        return request.createResponseBuilder(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(executionResult.toSpecification())
            .build();
    }
}
