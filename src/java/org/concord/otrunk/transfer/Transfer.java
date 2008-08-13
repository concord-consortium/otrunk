/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

package org.concord.otrunk.transfer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Transfer
{
    public static final int CHAR_BUFFER_SIZE = 1024;
    public static final int BYTE_BUFFER_SIZE = CHAR_BUFFER_SIZE * 64;
    protected Vector listeners = new Vector();
    protected TransferEvent currentEvent;
    protected int charBufferSize = CHAR_BUFFER_SIZE;
    protected int byteBufferSize = BYTE_BUFFER_SIZE;
	protected Object copier = null;
    
    public void transfer(Reader input, Writer output)
		throws IOException
    {
        try
        {
            char [] buffer = new char[charBufferSize];
            int total = 0;
            for (int i = input.read(buffer, 0, charBufferSize); i > 0; 
				 i = input.read(buffer, 0, charBufferSize))
            {
                total += i;
                output.write(buffer, 0, i);
                progressEvent(total);
            }
            output.flush();
            output.close();
            input.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
            e.printStackTrace();
			throw(e);
        }
    }

    public void transfer(InputStream input, OutputStream output)
    throws IOException
    {
    	transfer(input, output, true);
    }

    public void transfer(InputStream input, OutputStream output, boolean closeStreams)
    throws IOException
    {
        try
        {
            byte [] buffer = new byte[byteBufferSize];
            int total = 0;
            for (int i = input.read(buffer, 0, byteBufferSize); i > 0; 
				 i = input.read(buffer, 0, byteBufferSize))
            {
                total += i;
                output.write(buffer, 0, i);
                progressEvent(total);
            }
            if(closeStreams) {
            	output.flush();
            	output.close();
            	input.close();
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
            throw(e);
        }
    }
    
	public void transfer(File src, File dest)
	throws IOException
	{
		try
		{
			if (src.isDirectory())
			{
				if (dest.exists())
				{
					if (! dest.isDirectory())
					{
						throw new RuntimeException("Target not a directory");
					}
				}
				else
				{
					dest.mkdirs();
				}
				String [] fileList = src.list();
				for (int i = 0; i < fileList.length; i++)
				{
					transfer(new File(src, fileList[i]), new File(dest, fileList[i]));
				}
			}
			else
			{
				dest = new File(dest.getCanonicalPath());
				if(!dest.exists())
				{
					// Make any of the directories required for this file
					String destCanon = dest.getCanonicalPath();
					String parent = new File(destCanon).getParent();
					if(parent != null)
					{
						(new File(parent)).mkdirs();
					}
				}
				else
				{
					dest.delete();
				}
	
				startEvent(src.toString(), dest.toString(), (int)src.length());
				InputStream input = new FileInputStream(src);
				OutputStream output = new FileOutputStream(dest);
				transfer(input, output);
				finishEvent();
			}
		}
		catch (IOException e)
		{
			abortEvent(e);
			// clean up
			throw e;
		}
	}

	public void copy(File src, File dest)
		throws IOException
	{
		try
		{
			if(!src.exists())
			{
				throw new RuntimeException("Source doesn't exist");
			}

			if(dest.isDirectory())
			{
				String fileName = src.getName();
				File destFile = new File(dest, fileName);

				if(src.isDirectory())
				{
					// Check for infinite recursion
					String srcString = src.getCanonicalPath();
					String destString = dest.getCanonicalPath();
					if(destString.startsWith(srcString))
					{
						throw new RuntimeException("Dest directory is inside of src directory(infinite recursion)");
					}
					destFile.mkdir();
					// if(copier != null) copier.handleDirectories(src,dest);

					String [] fileList = src.list();
					for (int i = 0; i < fileList.length; i++)
					{
						copy(new File(src, fileList[i]), destFile);
					}
				}
				else
				{
					startEvent(src.toString(), destFile.toString(), (int)src.length());

					InputStream input = new FileInputStream(src);
					OutputStream output = new FileOutputStream(destFile);
					transfer(input, output);

					finishEvent();					
				}				
			}
			else if(dest.exists())
			{
				if(src.isDirectory())
				{
					throw new RuntimeException("Source is directory but destination is not");
				}
				File destParent = new File(new File(dest.getCanonicalPath()).getParent());
				copy(src, destParent);				
			}
			else
			{
				// I used getAbsolute here because some platforms complain when they try to get
				// a canonical path for a file that doesn't exist.
				File destAbsolute = new File(dest.getAbsolutePath());
				File destParent = new File(destAbsolute.getParent());
				if(!destParent.exists())
				{
					throw new RuntimeException("Cannot create destination file.  The parent directory " +
											   "of the destination doesn't exist");
				}
				copy(src, destParent);
			}

		}
		catch (IOException e)
		{
			abortEvent(e);
			// clean up
			throw e;
		}
	}
	
	public Vector getFileNames(String dirName, Vector fileList, boolean includeDirs)
	{
		File directory = new File(dirName);
		String [] fileNames = directory.list();
		for (int i = 0; i < fileNames.length; i++)
		{
			fileNames[i] = dirName + "/" + fileNames[i];
			File file = new File(fileNames[i]);
			if (file.isDirectory())
			{
				fileList = getFileNames(fileNames[i], fileList, includeDirs);
			}
			else
			{
				fileList.addElement(fileNames[i]);
			}
		}
		if (includeDirs)
			fileList.addElement(dirName);
		return fileList;
	}
	
	public String [] getFileNames(String dirName, boolean includeDirs)
	{
		Vector fileList = getFileNames(dirName, new Vector(), includeDirs);
		String [] fileNames = new String[fileList.size()];
		for (int i = 0; i < fileNames.length; i++)
		{
			fileNames[i] = (String) fileList.elementAt(i);
		}
		return fileNames;
	}
	
	public String [] getFileNames(String dirName)
	{
		return getFileNames(dirName, false);
	}
	
	public File [] getFiles(String dirName, boolean includeDirs)
	{
		Vector fileList = getFileNames(dirName, new Vector(), includeDirs);
		File [] files = new File[fileList.size()];
		for (int i = 0; i < files.length; i++)
		{
			files[i] = new File((String) fileList.elementAt(i));
		}
		return files;
	}
	
	public File [] getFiles(String dirName)
	{
		return getFiles(dirName, false);
	}

    public void setBufferSize(int size)
    {
        charBufferSize = size;
        byteBufferSize = 4 * size;
    }
    
    public void addTransferListener(TransferListener listener)
    {
        listeners.addElement(listener);
    }
    
    public void removeTransferListener(TransferListener listener)
    {
        listeners.removeElement(listener);
    }
    
    public void checkEvent(String sourceContent, String contentName)
    {
        currentEvent = new TransferEvent(this, sourceContent, contentName, 0);
        for (int i = 0; i < listeners.size(); i++)
        {
            TransferListener listener = (TransferListener) listeners.elementAt(i);
            listener.transferCheck(currentEvent);
        }
    }
    
	/**
	 * if the length is -1 then the length is unknown
	 */
    public void startEvent(String sourceContent, String contentName, int length)
    {
        if (currentEvent == null)
            currentEvent = new TransferEvent(this, sourceContent, contentName, length);
        else
        {
            currentEvent.setSourceContent(sourceContent);
            currentEvent.setContentName(contentName);
            currentEvent.setContentLength(length);
        }
        for (int i = 0; i < listeners.size(); i++)
        {
            TransferListener listener = (TransferListener) listeners.elementAt(i);
            listener.transferStarted(currentEvent);
        }
    }
    
    public void progressEvent(int currentTotal)
    {
        if (currentEvent instanceof TransferEvent)
        {
            currentEvent.setProgress(currentTotal);
            for (int i = 0; i < listeners.size(); i++)
            {
                TransferListener listener = (TransferListener) listeners.elementAt(i);
                listener.transferProgress(currentEvent);
            }
        }
    }
    
    public void finishEvent()
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            TransferListener listener = (TransferListener) listeners.elementAt(i);
            listener.transferFinished(currentEvent);
        }
    }
    
    public void abortEvent(Exception e)
    {
        currentEvent.setException(e);
        for (int i = 0; i < listeners.size(); i++)
        {
            TransferListener listener = (TransferListener) listeners.elementAt(i);
            listener.transferAborted(currentEvent);
        }
    }
    
    public Transfer.Server createTransferServer(int port)
    {
        return new Transfer.Server(this, port);
    }
    
    public Transfer.Client createTransferClient(String host, int port, String dirName)
    {
        return new Transfer.Client(this, host, port, dirName);
    }
    
    public static void main(String [] args)
    {
        if (args.length > 0)
        {
			Transfer transfer = new Transfer();
            if (args[0].toLowerCase().equals("client"))
            {
                Transfer.Client client = transfer.createTransferClient(args[1], Integer.parseInt(args[2]), args[3]);
            }
            else if (args[0].toLowerCase().equals("server"))
            {
                Transfer.Server server = transfer.createTransferServer(Integer.parseInt(args[1]));
            }
        }
    }
		
	public class Server
	implements Runnable
	{
		protected Socket clientSocket;
		protected Thread serverThread;
		protected Vector listeners = new Vector();
		protected Transfer transfer;
		
		public Server(Transfer transfer, int port)
		{
			this.transfer = transfer;
			try
			{
				ServerSocket socket = new ServerSocket(port);
				while (true)
				{
					try
					{
						clientSocket = socket.accept();
						serverThread = new Thread(this);
						serverThread.start();
					}
					catch (Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		public void run()
		{
			String fileName = "<No file>";
			int length = 0;
			try
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				fileName = input.readLine();
				length = Integer.parseInt(input.readLine());
				startEvent(fileName, fileName, length);
				File file = new File(fileName);
				File path = new File(file.getCanonicalPath());
				String parent = path.getParent();
				if (parent instanceof String)
				{
					File directory = new File(parent);
					directory.mkdirs();
				}
				FileWriter output = new FileWriter(file);
				transfer(input, output);
				finishEvent();
			}
			catch (Exception e)
			{
				abortEvent(e);
			}
		}
	}
	
	public class Client
	{
		protected Transfer transfer;
		
		public Client(Transfer transfer, String host, int port, String dirName)
		{
			this.transfer = transfer;
			File directory = new File(dirName);
			if (directory.isDirectory())
			{
				transferFiles(host, port, getFileNames(dirName));
			}
		}
		
		protected void transferFiles(String host, int port, String [] fileNames)
		{
			for (int i = 0; i < fileNames.length; i++)
			{
				try
				{
					Socket socket = new Socket(host, port);
					File file = new File(fileNames[i]);
					int length = (int) file.length();
					FileReader input = new FileReader(file);
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					startEvent(host + ":" + port + "/" + fileNames[i], fileNames[i], length);
					output.write(fileNames[i]);
					output.newLine();
					output.write("" + length);
					output.newLine();
					output.flush();
					transfer(input, output);
					finishEvent();
				}
				catch (Exception e)
				{
					abortEvent(e);
				}
			}
		}
	}
}

	

