package com.panasi.qna.pdf.service;

import java.io.IOException;
import java.util.List;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.panasi.qna.pdf.model.AnswerDTO;
import com.panasi.qna.pdf.model.QuestionWithAnswersDTO;

public final class PDFService {
	
	private PDFService() {
	    throw new IllegalStateException("Utility class");
	  }

	private static final String ARIAL = "./src/main/resources/fonts/Arial.ttf";

	private static PdfFont createFont() throws IOException {
		return PdfFontFactory.createFont(ARIAL);
	}

	private static void addTitle(Document document, String title) throws IOException {
		Paragraph titleParagraph = new Paragraph(title);
		titleParagraph.setFont(createFont());
		titleParagraph.setFontSize(16);
		titleParagraph.setBold();
		titleParagraph.setTextAlignment(TextAlignment.CENTER);
		titleParagraph.setMarginBottom(5);
		document.add(titleParagraph);
	}

	private static void addList(Document document, List<QuestionWithAnswersDTO> questions) throws IOException {

		for (int i = 0; i < questions.size(); i++) {

			QuestionWithAnswersDTO question = questions.get(i);
			Paragraph questionParagraph = new Paragraph(i + 1 + ". " + question.getContent());
			questionParagraph.setFont(createFont());
			questionParagraph.setFontSize(14);
			questionParagraph.setMarginTop(10);
			questionParagraph.setMarginBottom(5);
			document.add(questionParagraph);

			List<AnswerDTO> answers = question.getAnswers();
			com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
			list.setFont(createFont());
			list.setFontSize(11);
			list.setItalic();
			list.setTextAlignment(TextAlignment.JUSTIFIED);

			answers.forEach(answer -> {
				ListItem listItem = new ListItem(answer.getContent());
				listItem.setMarginBottom(5);
				list.add(listItem);
			});
			document.add(list);

		}

	}

	public static void createPDF(String fileName, String title, List<QuestionWithAnswersDTO> questions)
			throws IOException {
		
		String filePath = "./src/main/resources/temp/" + fileName;
		PdfWriter writer = new PdfWriter(filePath);
		PdfDocument pdfDoc = new PdfDocument(writer);
		pdfDoc.addNewPage();
		Document document = new Document(pdfDoc);

		addTitle(document, title);
		addList(document, questions);

		document.close();

	}

}
