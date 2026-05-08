package com.siemens.trainticketing.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.siemens.trainticketing.entity.Booking;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateTicketPdf(Booking booking) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("TRAIN TICKET CONFIRMATION"));
            document.add(new Paragraph("Booking ID: " + booking.getId()));
            document.add(new Paragraph("Train: " + booking.getTrain().getName()));
            document.add(new Paragraph("From: " + booking.getStartStationName()));
            document.add(new Paragraph("To: " + booking.getEndStationName()));
            document.add(new Paragraph("Tickets: " + booking.getNumberOfTickets()));
            document.add(new Paragraph(" "));

            String qrContent = "Valid Ticket | ID: " + booking.getId() + " | Train: " + booking.getTrain().getName();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOut);

            Image qrImage = Image.getInstance(pngOut.toByteArray());
            document.add(qrImage);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}