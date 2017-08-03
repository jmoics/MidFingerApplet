package pe.com.fingerprint.applet;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JButton;

import org.apache.commons.codec.binary.Base64;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import pe.com.fingerprint.util.Constants;
import pe.com.fingerprint.util.FingerPrintAppletException;

/**
 * @author Jorge
 *
 */
public class ValidateLaptopApplet
    extends JApplet
{
    private JButton jBtnValidate;

    public ValidateLaptopApplet() {
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        super.init();

        try {
            getJContentPane();
        } catch (final FingerPrintAppletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendResult() {
        final byte[] fileId = getClienteIdFile();
        final String strId = Base64.encodeBase64String(fileId);
        try {
            final JSObject jso = JSObject.getWindow(this);
            jso.call("notifyServer", strId);
            System.out.println("notifyServer Fired!");
        } catch(final JSException ex) {
            System.out.println("Could not create JS Object. Javascript Disabled!");
        }
    }

    private byte[] getClienteIdFile()
    {
        final String path = Constants.SYSTEM_STORE_IDPATH + File.separator + "store";
        final File file = new File(path);
        byte[] ret;
        try {
            final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n;
            while ((n = bis.read()) != -1) {
                baos.write(n);
            }
            ret = baos.toByteArray();
            bis.close();
            baos.close();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return ret;
    }

    private JButton getJBtnValidate() {
        if (jBtnValidate == null) {
            jBtnValidate = new JButton("Asignar Laptop");
            jBtnValidate.setBounds(new Rectangle(0, 0, 120, 24));
            jBtnValidate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    sendResult();
                }
            });
        }
        return jBtnValidate;
    }

    private void getJContentPane()
        throws FingerPrintAppletException
    {
        this.setSize(110, 30);
        this.setLayout(null);
        getContentPane().add(getJBtnValidate());
    }
}
