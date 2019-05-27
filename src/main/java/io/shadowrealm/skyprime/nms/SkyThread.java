package io.shadowrealm.skyprime.nms;

import java.util.UUID;

import mortar.lang.collection.GList;

public class SkyThread extends Thread
{
	private GList<Runnable> queue;

	public SkyThread(UUID id)
	{
		setName("Virtual Island " + id.toString());
		setPriority(MIN_PRIORITY);
		queue = new GList<>();
	}

	public void q(Runnable r)
	{
		queue.add(r);
	}

	@Override
	public void run()
	{
		while(!interrupted())
		{
			try
			{
				if(queue.isEmpty())
				{
					Thread.sleep(1);
					continue;
				}

				while(!queue.isEmpty())
				{
					try
					{
						queue.pop().run();
					}

					catch(Throwable e)
					{
						System.out.println("SkyThread runnable error " + getName());
						e.printStackTrace();
					}
				}
			}

			catch(InterruptedException e)
			{

			}

			catch(Throwable e)
			{
				System.out.println("SkyThread error " + getName());
				e.printStackTrace();
			}
		}
	}
}
