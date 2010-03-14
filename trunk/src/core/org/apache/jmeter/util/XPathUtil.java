/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class provides a few utility methods for dealing with XML/XPath.
 */
public class XPathUtil {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private XPathUtil() {
        super();
    }

    private static DocumentBuilderFactory documentBuilderFactory;

    /**
     * Returns a suitable document builder factory.
     * Caches the factory in case the next caller wants the same options.
     *
     * @param validate should the parser validate documents?
     * @param whitespace should the parser eliminate whitespace in element content?
     * @param namespace should the parser be namespace aware?
     *
     * @return javax.xml.parsers.DocumentBuilderFactory
     */
    private static synchronized DocumentBuilderFactory makeDocumentBuilderFactory(boolean validate, boolean whitespace,
            boolean namespace) {
        if (XPathUtil.documentBuilderFactory == null || documentBuilderFactory.isValidating() != validate
                || documentBuilderFactory.isNamespaceAware() != namespace
                || documentBuilderFactory.isIgnoringElementContentWhitespace() != whitespace) {
            // configure the document builder factory
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(validate);
            documentBuilderFactory.setNamespaceAware(namespace);
            documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
        }
        return XPathUtil.documentBuilderFactory;
    }

    /**
     * Create a DocumentBuilder using the makeDocumentFactory func.
     *
     * @param validate should the parser validate documents?
     * @param whitespace should the parser eliminate whitespace in element content?
     * @param namespace should the parser be namespace aware?
     * @return document builder
     * @throws ParserConfigurationException
     */
    public static synchronized DocumentBuilder makeDocumentBuilder(boolean validate, boolean whitespace, boolean namespace)
            throws ParserConfigurationException {
        // N.B. the factory is re-usable, but not necessarily thread-safe, so
        // the method is synchronized to protect the creation of the builder
        DocumentBuilder builder = makeDocumentBuilderFactory(validate, whitespace, namespace).newDocumentBuilder();
        builder.setErrorHandler(new MyErrorHandler(validate, false));
        return builder;
    }

    /**
     * Utility function to get new Document
     *
     * @param stream
     *            Document Input stream
     * @param validate
     *            Validate Document (not Tidy)
     * @param whitespace
     *            Element Whitespace (not Tidy)
     * @param namespace
     *            Is Namespace aware.
     * @param tolerant
     *            Is tolerant - i.e. use the Tidy parser
     *
     * @return document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TidyException
     */
    public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
            boolean tolerant) throws ParserConfigurationException, SAXException, IOException, TidyException {
        return makeDocument(stream, validate, whitespace, namespace, tolerant, true, false, false, false);

    }

    /**
     * Utility function to get new Document
     *
     * @param stream - Document Input stream
     * @param validate - Validate Document (not Tidy)
     * @param whitespace - Element Whitespace (not Tidy)
     * @param namespace - Is Namespace aware. (not Tidy)
     * @param tolerant - Is tolerant - i.e. use the Tidy parser
     * @param quiet - set Tidy quiet
     * @param showWarnings - set Tidy warnings
     * @param report_errors - throw TidyException if Tidy detects an error
     * @return document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TidyException
     */
    public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
            boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors)
            throws ParserConfigurationException, SAXException, IOException, TidyException {
                return makeDocument(stream, validate, whitespace, namespace,
                        tolerant, quiet, showWarnings, report_errors, false);
            }

    /**
     * Utility function to get new Document
     *
     * @param stream - Document Input stream
     * @param validate - Validate Document (not Tidy)
     * @param whitespace - Element Whitespace (not Tidy)
     * @param namespace - Is Namespace aware. (not Tidy)
     * @param tolerant - Is tolerant - i.e. use the Tidy parser
     * @param quiet - set Tidy quiet
     * @param showWarnings - set Tidy warnings
     * @param report_errors - throw TidyException if Tidy detects an error
     * @param isXml - is document already XML (Tidy only)
     * @return document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TidyException
     */
    public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
            boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml)
            throws ParserConfigurationException, SAXException, IOException, TidyException {
        Document doc;
        if (tolerant) {
            doc = tidyDoc(stream, quiet, showWarnings, report_errors, isXml);
        } else {
            doc = makeDocumentBuilder(validate, whitespace, namespace).parse(stream);
        }
        return doc;
    }

    /**
     * Create a document using Tidy
     *
     * @param stream - input
     * @param quiet - set Tidy quiet?
     * @param showWarnings - show Tidy warnings?
     * @param report_errors - log errors and throw TidyException?
     * @param isXML - treat document as XML?
     * @return the document
     *
     * @throws TidyException if a ParseError is detected and report_errors is true
     */
    private static Document tidyDoc(InputStream stream, boolean quiet, boolean showWarnings, boolean report_errors,
            boolean isXML) throws TidyException {
        StringWriter sw = new StringWriter();
        Tidy tidy = makeTidyParser(quiet, showWarnings, isXML, sw);
        Document doc = tidy.parseDOM(stream, null);
        doc.normalize();
        if (tidy.getParseErrors() > 0) {
            if (report_errors) {
                log.error("TidyException: " + sw.toString());
                throw new TidyException(tidy.getParseErrors(),tidy.getParseWarnings());
            }
            log.warn("Tidy errors: " + sw.toString());
        }
        return doc;
    }

    /**
     * Create a Tidy parser with the specified settings.
     *
     * @param quiet - set the Tidy quiet flag?
     * @param showWarnings - show Tidy warnings?
     * @param isXml - treat the content as XML?
     * @param stringWriter - if non-null, use this for Tidy errorOutput
     * @return the Tidy parser
     */
    public static Tidy makeTidyParser(boolean quiet, boolean showWarnings, boolean isXml, StringWriter stringWriter) {
        Tidy tidy = new Tidy();
        tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
        tidy.setQuiet(quiet);
        tidy.setShowWarnings(showWarnings);
        tidy.setMakeClean(true);
        tidy.setXmlTags(isXml);
        if (stringWriter != null) {
            tidy.setErrout(new PrintWriter(stringWriter));
        }
        return tidy;
    }

    static class MyErrorHandler implements ErrorHandler {
        private final boolean val, tol;

        private final String type;

        MyErrorHandler(boolean validate, boolean tolerate) {
            val = validate;
            tol = tolerate;
            type = "Val=" + val + " Tol=" + tol;
        }

        public void warning(SAXParseException ex) throws SAXException {
            log.info("Type=" + type + " " + ex);
            if (val && !tol){
                throw new SAXException(ex);
            }
        }

        public void error(SAXParseException ex) throws SAXException {
            log.warn("Type=" + type + " " + ex);
            if (val && !tol) {
                throw new SAXException(ex);
            }
        }

        public void fatalError(SAXParseException ex) throws SAXException {
            log.error("Type=" + type + " " + ex);
            if (val && !tol) {
                throw new SAXException(ex);
            }
        }
    }
}