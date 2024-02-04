package rocks.artur.endpoints;


import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import rocks.artur.api_impl.filter.AndFilterCriteria;
import rocks.artur.api_impl.filter.FilterOperation;
import rocks.artur.api_impl.filter.OrFilterCriteria;
import rocks.artur.api_impl.filter.SingleFilterCriteria;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CriteriaParser {
    private static Map<String, Operator> operations;
    private static final Logger LOG = LoggerFactory.getLogger(CriteriaParser.class);
    private static Pattern Regex = Pattern.compile("^(\\w+?)(" + Joiner.on("|")
            .join(SearchOperation.SIMPLE_OPERATION_SET) + ")(\"|\')(.+?)(\"|\')$");

    private enum Operator {
        OR(1), AND(2);
        final int precedence;

        Operator(int p) {
            precedence = p;
        }
    }


    static {
        Map<String, Operator> tempMap = new HashMap<>();
        tempMap.put("AND", Operator.AND);
        tempMap.put("OR", Operator.OR);
        tempMap.put("and", Operator.AND);
        tempMap.put("or", Operator.OR);
        operations = Collections.unmodifiableMap(tempMap);

    }

    private static boolean isHigherPrecedence(String currOp, String prevOp) {
        boolean b = operations.containsKey(prevOp) && operations.get(prevOp).precedence >= operations.get(currOp).precedence;
        return b;
    }

    public FilterCriteria parse(String searchString) throws ParseException {
        if (searchString == null || searchString.isEmpty())
            return null;
        LOG.info("The filter to parse is: " + searchString);

        Map<String, String> quotes = new HashMap<>();
        String stringWithoutQuotes = extractStringTokens(searchString,quotes,"\"");
        LOG.debug("The filter without quotes is: " + stringWithoutQuotes);
        stringWithoutQuotes = stringWithoutQuotes
                .replace("(", " ( ")
                .replace(")", " ) ")
                .replace(" == ", "=")
                .replace(" < ", "<")
                .replace(" > ", ">")
                .replace(" <= ", "<=")
                .replace(" >= ", ">=")
                .replace("&&", "AND")
                .replace("||", "OR");

        Deque<Object> output = new LinkedList<>();
        Deque<String> stack = new LinkedList<>();

        String[] split = stringWithoutQuotes.split("\\s+");
        Arrays.stream(split).forEach(token -> {
            if (operations.containsKey(token)) {
                while (!stack.isEmpty() && isHigherPrecedence(token, stack.peek())) {
                    output.push(stack.pop().equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
                }
                stack.push(token.equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
            } else if (token.equals(SearchOperation.LEFT_PARANTHESIS)) {
                stack.push(SearchOperation.LEFT_PARANTHESIS);
            } else if (token.equals(SearchOperation.RIGHT_PARANTHESIS)) {
                while (!stack.peek().equals((SearchOperation.LEFT_PARANTHESIS))) {
                    output.push(stack.pop());
                }
                stack.pop();
            } else {
                Matcher matcher = Regex.matcher(token);
                while (matcher.find()) {
                    String key = matcher.group(1);
                    String operation = matcher.group(2);
                    String value = matcher.group(4);
                    if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1,value.length());
                    }
                    if (quotes.containsKey(value)){
                        value = quotes.get(value);
                    }
                    output.push(new SingleFilterCriteria(Property.valueOf(key.toUpperCase()), SearchOperation.getSimpleOperation(operation), value));
                }
            }


        });

        while (!stack.isEmpty()) {
            output.push(stack.pop());
        }


        Collections.reverse((List<?>) output);

        Deque<FilterCriteria> specStack = new LinkedList<>();

        while (!output.isEmpty()) {
            Object mayBeOperand = output.pop();

            if (!(mayBeOperand instanceof String)) {
                specStack.push((FilterCriteria) mayBeOperand);
            } else {
                FilterCriteria pop1 = specStack.pop();
                FilterCriteria pop2 = specStack.pop();
                if (mayBeOperand.equals(SearchOperation.AND_OPERATOR)) {
                    AndFilterCriteria<CharacterisationResult> andFilterCriteria = new AndFilterCriteria<>(pop1, pop2);
                    specStack.push(andFilterCriteria);
                } else if (mayBeOperand.equals(SearchOperation.OR_OPERATOR)) {
                    OrFilterCriteria<CharacterisationResult> orFilterCriteria = new OrFilterCriteria<>(pop1, pop2);
                    specStack.push(orFilterCriteria);
                }

            }

        }

        if (null == specStack || specStack.isEmpty()) {
            throw new ParseException("The filter string is incorrect", 0);
        }
        FilterCriteria pop = specStack.pop();
        return pop;
    }

    private String extractStringTokens(String searchString, Map<String, String> tokens, String quoteSign) {
        int occurrences = StringUtils.countOccurrencesOf(searchString, quoteSign);
        if (occurrences % 2 == 1) {
            return searchString;
        }
        Pattern quotePattern = null;
        if (quoteSign.equals("\"")) {
            quotePattern = Pattern.compile("\"((?:[^\"]|\"\")*)\"");
        } else {
            quotePattern = Pattern.compile("'((?:[^']|'')*)'");
        }


        Matcher matcher = quotePattern.matcher(searchString);

        int index = 0;
        while (matcher.find()){
            String quotedValue = matcher.group();
            String group1 = matcher.group(1);
            String tokenKey="quote__"+index++;
            tokens.put(tokenKey, group1);
            searchString = searchString.replace(group1,tokenKey);
        }
        return searchString;
    }
}
